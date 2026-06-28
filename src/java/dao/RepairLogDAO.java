package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.RepairLog;
import util.DBContext;

public class RepairLogDAO {

    private RepairLog mapRow(ResultSet rs) throws SQLException {
        RepairLog l = new RepairLog();
        l.setLogID(rs.getInt("LogID"));
        l.setOrderID(rs.getInt("OrderID"));
        l.setStaffID(rs.getInt("StaffID"));
        l.setNote(rs.getString("Note"));
        l.setCreatedDate(rs.getTimestamp("CreatedDate"));
        try {
            l.setStaffName(rs.getString("FullName"));
        } catch (SQLException ignore) {
        }
        return l;
    }

    /** Staff adds an inspection/progress note to an order ("Create vehicle inspection report" use case). */
    public int insert(int orderID, int staffID, String note) {
        String sql = "INSERT INTO RepairLogs (OrderID, StaffID, Note, CreatedDate) VALUES (?, ?, ?, GETDATE())";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, orderID);
            ps.setInt(2, staffID);
            ps.setString(3, note);
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

    public List<RepairLog> getByOrderID(int orderID) {
        List<RepairLog> list = new ArrayList<>();
        String sql = "SELECT l.*, u.FullName FROM RepairLogs l "
                + "JOIN Users u ON l.StaffID = u.UserID WHERE l.OrderID = ? ORDER BY l.CreatedDate ASC";
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
}
