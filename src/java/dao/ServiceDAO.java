package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Service;
import util.DBContext;

public class ServiceDAO {

    private Service mapRow(ResultSet rs) throws SQLException {
        Service s = new Service();
        s.setServiceID(rs.getInt("ServiceID"));
        s.setServiceName(rs.getString("ServiceName"));
        s.setDescription(rs.getString("Description"));
        s.setPrice(rs.getBigDecimal("Price"));
        s.setActive(rs.getBoolean("IsActive"));
        return s;
    }

    /** activeOnly=true is used by Customer/Staff facing "view services" screens. */
    public List<Service> getAll(boolean activeOnly) {
        List<Service> list = new ArrayList<>();
        String sql = "SELECT * FROM Services" + (activeOnly ? " WHERE IsActive = 1" : "") + " ORDER BY ServiceName";
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

    public Service getByID(int serviceID) {
        String sql = "SELECT * FROM Services WHERE ServiceID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceID);
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

    public int insert(Service s) {
        String sql = "INSERT INTO Services (ServiceName, Description, Price, IsActive) VALUES (?, ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getServiceName());
            ps.setString(2, s.getDescription());
            ps.setBigDecimal(3, s.getPrice());
            ps.setBoolean(4, s.isActive());
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

    public boolean update(Service s) {
        String sql = "UPDATE Services SET ServiceName=?, Description=?, Price=?, IsActive=? WHERE ServiceID=?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getServiceName());
            ps.setString(2, s.getDescription());
            ps.setBigDecimal(3, s.getPrice());
            ps.setBoolean(4, s.isActive());
            ps.setInt(5, s.getServiceID());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /** Soft delete: deactivate instead of hard-delete to preserve OrderDetails history/FK integrity. */
    public boolean deactivate(int serviceID) {
        String sql = "UPDATE Services SET IsActive = 0 WHERE ServiceID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
