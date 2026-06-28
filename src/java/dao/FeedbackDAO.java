package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Feedback;
import util.DBContext;

public class FeedbackDAO {

    private Feedback mapRow(ResultSet rs) throws SQLException {
        Feedback f = new Feedback();
        f.setFeedbackID(rs.getInt("FeedbackID"));
        f.setOrderID(rs.getInt("OrderID"));
        f.setCustomerID(rs.getInt("CustomerID"));
        f.setRating(rs.getInt("Rating"));
        f.setComment(rs.getString("Comment"));
        f.setCreatedDate(rs.getTimestamp("CreatedDate"));
        try {
            f.setCustomerName(rs.getString("FullName"));
        } catch (SQLException ignore) {
        }
        return f;
    }

    /** Customer rates a completed order ("Rate service" use case). One feedback per order. */
    public int insert(int orderID, int customerID, int rating, String comment) {
        String sql = "INSERT INTO Feedback (OrderID, CustomerID, Rating, Comment, CreatedDate) "
                + "VALUES (?, ?, ?, ?, GETDATE())";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, orderID);
            ps.setInt(2, customerID);
            ps.setInt(3, rating);
            ps.setString(4, comment);
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

    public boolean existsForOrder(int orderID) {
        String sql = "SELECT COUNT(*) AS cnt FROM Feedback WHERE OrderID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /** Used by Admin "view customer rate service" screen. */
    public List<Feedback> getAll() {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT f.*, u.FullName FROM Feedback f "
                + "JOIN Users u ON f.CustomerID = u.UserID ORDER BY f.CreatedDate DESC";
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

    public double getAverageRating() {
        String sql = "SELECT AVG(CAST(Rating AS FLOAT)) AS avgRating FROM Feedback";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("avgRating");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
