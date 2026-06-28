package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.CustomerBooking;
import util.DBContext;

public class CustomerBookingDAO {

    private CustomerBooking mapRow(ResultSet rs) throws SQLException {
        CustomerBooking b = new CustomerBooking();
        b.setBookingID(rs.getInt("BookingID"));
        int custId = rs.getInt("CustomerID");
        b.setCustomerID(rs.wasNull() ? null : custId);
        b.setFullName(rs.getString("FullName"));
        b.setPhone(rs.getString("Phone"));
        b.setLicensePlate(rs.getString("LicensePlate"));
        b.setVehicleInfo(rs.getString("VehicleInfo"));
        b.setDescription(rs.getString("Description"));
        b.setPreferredDate(rs.getTimestamp("PreferredDate"));
        b.setStatus(rs.getString("Status"));
        b.setRejectReason(rs.getString("RejectReason"));
        b.setCreatedDate(rs.getTimestamp("CreatedDate"));
        int orderId = rs.getInt("RepairOrderID");
        b.setRepairOrderID(rs.wasNull() ? null : orderId);
        return b;
    }

    /** Customer creates a booking (Booking Form screen). */
    public int insert(CustomerBooking b) {
        String sql = "INSERT INTO CustomerBookings (CustomerID, FullName, Phone, LicensePlate, VehicleInfo, "
                + "Description, PreferredDate, Status, CreatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, 'PENDING', GETDATE())";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            if (b.getCustomerID() != null) {
                ps.setInt(1, b.getCustomerID());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, b.getFullName());
            ps.setString(3, b.getPhone());
            ps.setString(4, b.getLicensePlate());
            ps.setString(5, b.getVehicleInfo());
            ps.setString(6, b.getDescription());
            if (b.getPreferredDate() != null) {
                ps.setTimestamp(7, new Timestamp(b.getPreferredDate().getTime()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
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

    public CustomerBooking getByID(int bookingID) {
        String sql = "SELECT * FROM CustomerBookings WHERE BookingID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingID);
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

    /** Used by Admin Booking List screen. status=null/"" returns all. */
    public List<CustomerBooking> getAll(String status) {
        List<CustomerBooking> list = new ArrayList<>();
        String sql = "SELECT * FROM CustomerBookings "
                + (status != null && !status.isEmpty() ? "WHERE Status = ? " : "")
                + "ORDER BY CreatedDate DESC";
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

    /** Used by Customer "History booking" screen. */
    public List<CustomerBooking> getByCustomerID(int customerID) {
        List<CustomerBooking> list = new ArrayList<>();
        String sql = "SELECT * FROM CustomerBookings WHERE CustomerID = ? ORDER BY CreatedDate DESC";
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

    public boolean updateStatus(int bookingID, String status, String rejectReason) {
        String sql = "UPDATE CustomerBookings SET Status = ?, RejectReason = ? WHERE BookingID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, rejectReason);
            ps.setInt(3, bookingID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /** Called inside the same transaction/connection as AdminBookingActionServlet's conversion logic. */
    public boolean markConverted(Connection conn, int bookingID, int repairOrderID) throws SQLException {
        String sql = "UPDATE CustomerBookings SET Status = 'CONVERTED', RepairOrderID = ? WHERE BookingID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, repairOrderID);
            ps.setInt(2, bookingID);
            return ps.executeUpdate() > 0;
        }
    }

    /** Used inside a shared-connection transaction when linking a walk-in booking to a newly created customer. */
    public boolean linkCustomer(Connection conn, int bookingID, int customerID) throws SQLException {
        String sql = "UPDATE CustomerBookings SET CustomerID = ? WHERE BookingID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            ps.setInt(2, bookingID);
            return ps.executeUpdate() > 0;
        }
    }

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) AS cnt FROM CustomerBookings WHERE Status = ?";
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
}
