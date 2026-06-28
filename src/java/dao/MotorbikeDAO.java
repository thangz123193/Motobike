package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Motorbike;
import util.DBContext;

public class MotorbikeDAO {

    private Motorbike mapRow(ResultSet rs) throws SQLException {
        Motorbike m = new Motorbike();
        m.setMotorbikeID(rs.getInt("MotorbikeID"));
        m.setCustomerID(rs.getInt("CustomerID"));
        m.setLicensePlate(rs.getString("LicensePlate"));
        m.setBrand(rs.getString("Brand"));
        m.setModel(rs.getString("Model"));
        m.setColor(rs.getString("Color"));
        int year = rs.getInt("Year");
        m.setYear(rs.wasNull() ? null : year);
        try {
            m.setCustomerName(rs.getString("FullName"));
        } catch (SQLException ignore) {
        }
        return m;
    }

    public Motorbike getByPlate(String licensePlate) {
        String sql = "SELECT m.*, u.FullName FROM Motorbikes m "
                + "JOIN Users u ON m.CustomerID = u.UserID WHERE m.LicensePlate = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, licensePlate);
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

    public Motorbike getByID(int motorbikeID) {
        String sql = "SELECT m.*, u.FullName FROM Motorbikes m "
                + "JOIN Users u ON m.CustomerID = u.UserID WHERE m.MotorbikeID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, motorbikeID);
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

    public List<Motorbike> getByCustomerID(int customerID) {
        List<Motorbike> list = new ArrayList<>();
        String sql = "SELECT m.*, u.FullName FROM Motorbikes m "
                + "JOIN Users u ON m.CustomerID = u.UserID WHERE m.CustomerID = ? ORDER BY m.MotorbikeID DESC";
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

    public int insert(Motorbike m) {
        String sql = "INSERT INTO Motorbikes (CustomerID, LicensePlate, Brand, Model, Color, Year) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getCustomerID());
            ps.setString(2, m.getLicensePlate());
            ps.setString(3, m.getBrand());
            ps.setString(4, m.getModel());
            ps.setString(5, m.getColor());
            if (m.getYear() != null) {
                ps.setInt(6, m.getYear());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
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

    public boolean existsPlate(String licensePlate) {
        String sql = "SELECT COUNT(*) AS cnt FROM Motorbikes WHERE LicensePlate = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, licensePlate);
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
}
