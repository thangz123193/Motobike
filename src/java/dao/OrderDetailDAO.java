package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.OrderDetail;
import util.DBContext;

public class OrderDetailDAO {

    private OrderDetail mapRow(ResultSet rs) throws SQLException {
        OrderDetail d = new OrderDetail();
        d.setOrderDetailID(rs.getInt("OrderDetailID"));
        d.setOrderID(rs.getInt("OrderID"));
        d.setItemType(rs.getString("ItemType"));
        int serviceId = rs.getInt("ServiceID");
        d.setServiceID(rs.wasNull() ? null : serviceId);
        int partId = rs.getInt("PartID");
        d.setPartID(rs.wasNull() ? null : partId);
        d.setQuantity(rs.getInt("Quantity"));
        d.setUnitPrice(rs.getBigDecimal("UnitPrice"));
        d.setLineTotal(rs.getBigDecimal("LineTotal"));
        try {
            d.setItemName(rs.getString("ItemName"));
        } catch (SQLException ignore) {
        }
        return d;
    }

    public List<OrderDetail> getByOrderID(int orderID) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.*, "
                + "CASE WHEN od.ItemType='SERVICE' THEN s.ServiceName ELSE p.PartName END AS ItemName "
                + "FROM OrderDetails od "
                + "LEFT JOIN Services s ON od.ServiceID = s.ServiceID "
                + "LEFT JOIN Parts p ON od.PartID = p.PartID "
                + "WHERE od.OrderID = ? ORDER BY od.OrderDetailID";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /** Adds a SERVICE line. No stock concerns. */
    public int addServiceLine(int orderID, int serviceID, int quantity, BigDecimal unitPrice) {
        String sql = "INSERT INTO OrderDetails (OrderID, ItemType, ServiceID, Quantity, UnitPrice) "
                + "VALUES (?, 'SERVICE', ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, orderID);
            ps.setInt(2, serviceID);
            ps.setInt(3, quantity);
            ps.setBigDecimal(4, unitPrice);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Adds a PART line, deducting stock atomically first. If the stock deduction
     * fails (not enough quantity) or the INSERT fails after a successful deduction,
     * the caller (StaffAddPartServiceServlet) handles the compensating restock so the
     * two operations stay consistent even though they aren't wrapped in a single
     * DB transaction by this method.
     * Returns the new OrderDetailID, or -1 if stock was insufficient,
     * or -2 if the deduction succeeded but the insert failed (compensating restock already applied here).
     */
    public int addPartLine(int orderID, int partID, int quantity, BigDecimal unitPrice) {
        PartDAO partDAO = new PartDAO();
        try (Connection conn = new DBContext().getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean deducted = partDAO.deductStock(conn, partID, quantity);
                if (!deducted) {
                    conn.rollback();
                    return -1; // not enough stock
                }
                String sql = "INSERT INTO OrderDetails (OrderID, ItemType, PartID, Quantity, UnitPrice) "
                        + "VALUES (?, 'PART', ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, orderID);
                    ps.setInt(2, partID);
                    ps.setInt(3, quantity);
                    ps.setBigDecimal(4, unitPrice);
                    ps.executeUpdate();
                    int newId = -2;
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            newId = keys.getInt(1);
                        }
                    }
                    conn.commit();
                    return newId;
                }
            } catch (SQLException inner) {
                conn.rollback();
                inner.printStackTrace();
                return -2;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -2;
    }

    /** Sum of all line totals for an order (used to stamp RepairOrders.TotalCost on completion). */
    public BigDecimal sumTotalForOrder(int orderID) {
        String sql = "SELECT ISNULL(SUM(LineTotal), 0) AS total FROM OrderDetails WHERE OrderID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /** Removes a line. If it was a PART line, restocks the deducted quantity back. */
    public boolean deleteLine(int orderDetailID) {
        String selectSql = "SELECT ItemType, PartID, Quantity FROM OrderDetails WHERE OrderDetailID = ?";
        try (Connection conn = new DBContext().getConnection()) {
            conn.setAutoCommit(false);
            String itemType = null;
            Integer partId = null;
            int qty = 0;
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setInt(1, orderDetailID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        itemType = rs.getString("ItemType");
                        int p = rs.getInt("PartID");
                        partId = rs.wasNull() ? null : p;
                        qty = rs.getInt("Quantity");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM OrderDetails WHERE OrderDetailID = ?")) {
                del.setInt(1, orderDetailID);
                del.executeUpdate();
            }
            if ("PART".equals(itemType) && partId != null) {
                new PartDAO().restock(conn, partId, qty);
            }
            conn.commit();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
