package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.RepairOrder;
import util.DBContext;

public class RepairOrderDAO {

    private static final String BASE_SELECT =
            "SELECT o.*, cu.FullName AS CustomerName, cu.Phone AS CustomerPhone, "
            + "m.LicensePlate, st.FullName AS StaffName "
            + "FROM RepairOrders o "
            + "LEFT JOIN Users cu ON o.CustomerID = cu.UserID "
            + "LEFT JOIN Motorbikes m ON o.MotorbikeID = m.MotorbikeID "
            + "LEFT JOIN Users st ON o.AssignedStaffID = st.UserID ";

    private RepairOrder mapRow(ResultSet rs) throws SQLException {
        RepairOrder o = new RepairOrder();
        o.setOrderID(rs.getInt("OrderID"));
        int custId = rs.getInt("CustomerID");
        o.setCustomerID(rs.wasNull() ? null : custId);
        o.setCustomerName(rs.getString("CustomerName"));
        o.setCustomerPhone(rs.getString("CustomerPhone"));
        int bikeId = rs.getInt("MotorbikeID");
        o.setMotorbikeID(rs.wasNull() ? null : bikeId);
        o.setLicensePlate(rs.getString("LicensePlate"));
        int bookingId = rs.getInt("BookingID");
        o.setBookingID(rs.wasNull() ? null : bookingId);
        int staffId = rs.getInt("AssignedStaffID");
        o.setAssignedStaffID(rs.wasNull() ? null : staffId);
        o.setAssignedStaffName(rs.getString("StaffName"));
        o.setStatus(rs.getString("Status"));
        o.setDescription(rs.getString("Description"));
        o.setEstimatedCost(rs.getBigDecimal("EstimatedCost"));
        o.setTotalCost(rs.getBigDecimal("TotalCost"));
        o.setRejectReason(rs.getString("RejectReason"));
        o.setCreatedDate(rs.getTimestamp("CreatedDate"));
        o.setAssignedDate(rs.getTimestamp("AssignedDate"));
        o.setAcceptedDate(rs.getTimestamp("AcceptedDate"));
        o.setCompletedDate(rs.getTimestamp("CompletedDate"));
        return o;
    }

    public RepairOrder getByID(int orderID) {
        String sql = BASE_SELECT + "WHERE o.OrderID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /** Admin's full order list, optionally filtered by status. Used by AdminOrderListServlet. */
    public List<RepairOrder> getAll(String status) {
        List<RepairOrder> list = new ArrayList<>();
        String sql = BASE_SELECT + (status != null && !status.isEmpty() ? "WHERE o.Status = ? " : "")
                + "ORDER BY o.CreatedDate DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (status != null && !status.isEmpty()) {
                ps.setString(1, status);
            }
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

    /** Staff's assigned order list (Staff Order List screen). Optional status filter. */
    public List<RepairOrder> getByStaffID(int staffID, String status) {
        List<RepairOrder> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE o.AssignedStaffID = ? "
                + (status != null && !status.isEmpty() ? "AND o.Status = ? " : "")
                + "ORDER BY o.CreatedDate DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffID);
            if (status != null && !status.isEmpty()) {
                ps.setString(2, status);
            }
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

    /** Customer's own order/repair history. */
    public List<RepairOrder> getByCustomerID(int customerID) {
        List<RepairOrder> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE o.CustomerID = ? ORDER BY o.CreatedDate DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerID);
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

    /**
     * Creates a brand-new repair order. Used both for direct Admin-created orders
     * (walk-in, no booking) and inside the booking-conversion transaction.
     * If conn is provided (non-null), participates in the caller's transaction;
     * otherwise opens and manages its own connection.
     */
    public int insert(Connection conn, RepairOrder o) throws SQLException {
        String sql = "INSERT INTO RepairOrders (CustomerID, MotorbikeID, BookingID, AssignedStaffID, Status, "
                + "Description, EstimatedCost, TotalCost, CreatedDate, AssignedDate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 0, GETDATE(), ?)";
        boolean manageOwnConn = (conn == null);
        Connection c = manageOwnConn ? new DBContext().getConnection() : conn;
        try (PreparedStatement ps = c.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            if (o.getCustomerID() != null) {
                ps.setInt(1, o.getCustomerID());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            if (o.getMotorbikeID() != null) {
                ps.setInt(2, o.getMotorbikeID());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            if (o.getBookingID() != null) {
                ps.setInt(3, o.getBookingID());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            if (o.getAssignedStaffID() != null) {
                ps.setInt(4, o.getAssignedStaffID());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, o.getStatus() != null ? o.getStatus() : "PENDING");
            ps.setString(6, o.getDescription());
            ps.setBigDecimal(7, o.getEstimatedCost());
            // AssignedDate is set now only if a staff is already assigned at creation time
            if (o.getAssignedStaffID() != null) {
                ps.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } finally {
            if (manageOwnConn && c != null) {
                c.close();
            }
        }
        return -1;
    }

    /** Admin assigns/reassigns a staff to an order. Resets the workflow back to PENDING for the new staff to accept. */
    public boolean assignStaff(int orderID, int staffID) {
        String sql = "UPDATE RepairOrders SET AssignedStaffID = ?, Status = 'PENDING', AssignedDate = GETDATE(), "
                + "AcceptedDate = NULL, RejectReason = NULL WHERE OrderID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffID);
            ps.setInt(2, orderID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /** Staff accepts an assigned order: PENDING -> ACCEPTED. */
    public boolean accept(int orderID, int staffID) {
        String sql = "UPDATE RepairOrders SET Status = 'ACCEPTED', AcceptedDate = GETDATE() "
                + "WHERE OrderID = ? AND AssignedStaffID = ? AND Status = 'PENDING'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            ps.setInt(2, staffID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /** Staff rejects an assigned order: PENDING -> REJECTED (branch state). */
    public boolean reject(int orderID, int staffID, String reason) {
        String sql = "UPDATE RepairOrders SET Status = 'REJECTED', RejectReason = ? "
                + "WHERE OrderID = ? AND AssignedStaffID = ? AND Status = 'PENDING'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reason);
            ps.setInt(2, orderID);
            ps.setInt(3, staffID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /** Staff starts work: ACCEPTED -> PROCESSING. */
    public boolean startProcessing(int orderID, int staffID) {
        String sql = "UPDATE RepairOrders SET Status = 'PROCESSING' "
                + "WHERE OrderID = ? AND AssignedStaffID = ? AND Status = 'ACCEPTED'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            ps.setInt(2, staffID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /** Staff completes the order: PROCESSING -> COMPLETED, and stamps the final TotalCost (sum of OrderDetails). */
    public boolean complete(int orderID, int staffID, java.math.BigDecimal totalCost) {
        String sql = "UPDATE RepairOrders SET Status = 'COMPLETED', CompletedDate = GETDATE(), TotalCost = ? "
                + "WHERE OrderID = ? AND AssignedStaffID = ? AND Status = 'PROCESSING'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, totalCost);
            ps.setInt(2, orderID);
            ps.setInt(3, staffID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /** Admin deletes an order (extension point: "Delete repair order" <<Extend>> Manage repair order in the use case diagram). */
    public boolean delete(int orderID) {
        String sql = "DELETE FROM RepairOrders WHERE OrderID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Likely blocked by FK (OrderDetails/RepairLogs/Feedback reference this order)
        }
        return false;
    }

    /** Dashboard/report counts by status, used by Admin financial report and home dashboards. */
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) AS cnt FROM RepairOrders WHERE Status = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /** Sum of TotalCost for COMPLETED orders within an optional date range (both nullable = all time). Used in financial report. */
    public java.math.BigDecimal sumRevenue(java.sql.Date from, java.sql.Date to) {
        StringBuilder sql = new StringBuilder(
                "SELECT ISNULL(SUM(TotalCost),0) AS revenue FROM RepairOrders WHERE Status = 'COMPLETED' ");
        if (from != null) {
            sql.append("AND CompletedDate >= ? ");
        }
        if (to != null) {
            sql.append("AND CompletedDate <= ? ");
        }
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (from != null) {
                ps.setDate(idx++, from);
            }
            if (to != null) {
                ps.setDate(idx++, to);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("revenue");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return java.math.BigDecimal.ZERO;
    }
}
