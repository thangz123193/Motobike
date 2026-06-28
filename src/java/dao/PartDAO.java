package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Part;
import util.DBContext;

public class PartDAO {

    private Part mapRow(ResultSet rs) throws SQLException {
        Part p = new Part();
        p.setPartID(rs.getInt("PartID"));
        p.setPartName(rs.getString("PartName"));
        p.setDescription(rs.getString("Description"));
        p.setUnit(rs.getString("Unit"));
        p.setPrice(rs.getBigDecimal("Price"));
        p.setQuantity(rs.getInt("Quantity"));
        p.setActive(rs.getBoolean("IsActive"));
        return p;
    }

    public List<Part> getAll(boolean activeOnly) {
        List<Part> list = new ArrayList<>();
        String sql = "SELECT * FROM Parts" + (activeOnly ? " WHERE IsActive = 1" : "") + " ORDER BY PartName";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public Part getByID(int partID) {
        String sql = "SELECT * FROM Parts WHERE PartID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partID);
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

    public int insert(Part p) {
        String sql = "INSERT INTO Parts (PartName, Description, Unit, Price, Quantity, IsActive) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getPartName());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getUnit());
            ps.setBigDecimal(4, p.getPrice());
            ps.setInt(5, p.getQuantity());
            ps.setBoolean(6, p.isActive());
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

    public boolean update(Part p) {
        String sql = "UPDATE Parts SET PartName=?, Description=?, Unit=?, Price=?, Quantity=?, IsActive=? WHERE PartID=?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPartName());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getUnit());
            ps.setBigDecimal(4, p.getPrice());
            ps.setInt(5, p.getQuantity());
            ps.setBoolean(6, p.isActive());
            ps.setInt(7, p.getPartID());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean deactivate(int partID) {
        String sql = "UPDATE Parts SET IsActive = 0 WHERE PartID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /** Add stock (admin restock). */
    public boolean addStock(int partID, int amount) {
        String sql = "UPDATE Parts SET Quantity = Quantity + ? WHERE PartID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, partID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Atomically deducts stock only if enough quantity is available.
     * The WHERE Quantity >= requested clause prevents the stock from
     * going negative under concurrent requests (no separate check-then-act race).
     * Returns true if the deduction succeeded (i.e., enough stock existed).
     */
    public boolean deductStock(Connection conn, int partID, int requestedQty) throws SQLException {
        String sql = "UPDATE Parts SET Quantity = Quantity - ? WHERE PartID = ? AND Quantity >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestedQty);
            ps.setInt(2, partID);
            ps.setInt(3, requestedQty);
            return ps.executeUpdate() > 0;
        }
    }

    /** Compensating restock, used if a later step in the same business transaction fails. */
    public boolean restock(Connection conn, int partID, int qty) throws SQLException {
        String sql = "UPDATE Parts SET Quantity = Quantity + ? WHERE PartID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, partID);
            return ps.executeUpdate() > 0;
        }
    }
}
