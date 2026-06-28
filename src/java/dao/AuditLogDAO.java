package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import util.DBContext;

public class AuditLogDAO {

    /** Fire-and-forget audit trail write. Call after sensitive Admin/Staff actions. */
    public void log(Integer userID, String action, String detail) {
        String sql = "INSERT INTO AuditLogs (UserID, Action, Detail, CreatedDate) VALUES (?, ?, ?, GETDATE())";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userID != null) {
                ps.setInt(1, userID);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, action);
            ps.setString(3, detail);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Audit logging failures should never break the main business flow
        }
    }
}
