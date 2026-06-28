package controller.admin;

import dao.AuditLogDAO;
import dao.CustomerBookingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import model.CustomerBooking;
import model.User;
import util.DBContext;
import util.ServletUtils;

/**
 * Handles two actions on a CustomerBooking:
 *
 *   action=reject  -> simply marks the booking REJECTED with a reason.
 *
 *   action=confirm -> the multi-step booking-to-order conversion:
 *     1. Resolve the Customer by phone number:
 *          - if a User with that phone (RoleID=Customer) already exists, reuse it
 *          - otherwise auto-create a new Customer account (username = phone number,
 *            a random temp password) so the booking always ends up linked to a real account
 *     2. Resolve the Motorbike by license plate:
 *          - if a Motorbike with that plate exists, reuse it (and re-link to the
 *            resolved customer if it was previously unowned)
 *          - otherwise create a new Motorbike row under the resolved customer
 *     3. Create a new RepairOrder (status PENDING, linked to BookingID, MotorbikeID, CustomerID)
 *     4. Update the CustomerBooking: Status='CONVERTED', RepairOrderID=<new id>, CustomerID=<resolved>
 *
 * All four steps run on a single shared Connection with manual commit/rollback,
 * so a failure partway through (e.g. step 3 throws) leaves no partial Customer/
 * Motorbike/Order rows behind.
 */
@WebServlet(name = "AdminBookingActionServlet", urlPatterns = {"/admin/booking-action"})
public class AdminBookingActionServlet extends HttpServlet {

    private static final int CUSTOMER_ROLE_ID = 3;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User admin = (User) session.getAttribute("user");

        int bookingID = ServletUtils.getParamInt(req, "bookingId", -1);
        String action = ServletUtils.getParamTrimmed(req, "action");

        CustomerBookingDAO bookingDAO = new CustomerBookingDAO();
        CustomerBooking booking = bookingDAO.getByID(bookingID);

        if (booking == null || !"PENDING".equals(booking.getStatus())) {
            resp.sendRedirect(req.getContextPath() + "/admin/bookings?error=1");
            return;
        }

        if ("reject".equals(action)) {
            String reason = ServletUtils.getParamTrimmed(req, "reason");
            boolean ok = bookingDAO.updateStatus(bookingID, "REJECTED", reason);
            if (ok) {
                new AuditLogDAO().log(admin.getUserID(), "REJECT_BOOKING",
                        "Booking #" + bookingID + " rejected: " + reason);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/bookings?" + (ok ? "success=1" : "error=1"));
            return;
        }

        if (!"confirm".equals(action)) {
            resp.sendRedirect(req.getContextPath() + "/admin/bookings?error=1");
            return;
        }

        // Allow admin to override estimated cost / assign staff right at conversion time (optional)
        BigDecimal estimatedCost = ServletUtils.parseDecimalOrDefault(
                req.getParameter("estimatedCost"), BigDecimal.ZERO);
        Integer assignedStaffId = null;
        int staffParam = ServletUtils.getParamInt(req, "assignedStaffId", -1);
        if (staffParam > 0) {
            assignedStaffId = staffParam;
        }

        try (Connection conn = new DBContext().getConnection()) {
            conn.setAutoCommit(false);
            try {
                int customerId = resolveOrCreateCustomer(conn, booking.getPhone(), booking.getFullName());
                int motorbikeId = resolveOrCreateMotorbike(conn, booking.getLicensePlate(),
                        booking.getVehicleInfo(), customerId);

                int orderId = insertRepairOrder(conn, customerId, motorbikeId, bookingID,
                        assignedStaffId, booking.getDescription(), estimatedCost);

                new CustomerBookingDAO().markConverted(conn, bookingID, orderId);
                if (booking.getCustomerID() == null) {
                    new CustomerBookingDAO().linkCustomer(conn, bookingID, customerId);
                }

                conn.commit();
                new AuditLogDAO().log(admin.getUserID(), "CONVERT_BOOKING",
                        "Booking #" + bookingID + " converted to Order #" + orderId);
                resp.sendRedirect(req.getContextPath() + "/admin/order-detail?id=" + orderId + "&success=1");
                return;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        resp.sendRedirect(req.getContextPath() + "/admin/bookings?error=1");
    }

    /** Finds a Customer by phone, or creates a minimal new Customer account if none exists. */
    private int resolveOrCreateCustomer(Connection conn, String phone, String fullName) throws SQLException {
        String selectSql = "SELECT UserID FROM Users WHERE Phone = ? AND RoleID = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, phone);
            ps.setInt(2, CUSTOMER_ROLE_ID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UserID");
                }
            }
        }

        // No existing account for this phone number -> auto-create one.
        // Username defaults to the phone number; temp password is the phone number too,
        // the customer can change it later after Admin shares the credentials.
        String username = phone;
        String tempPassword = phone;
        String insertSql = "INSERT INTO Users (Username, Password, FullName, Phone, RoleID, IsActive) "
                + "VALUES (?, ?, ?, ?, ?, 1)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, tempPassword);
            ps.setString(3, fullName);
            ps.setString(4, phone);
            ps.setInt(5, CUSTOMER_ROLE_ID);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to resolve or create customer for phone " + phone);
    }

    /** Finds a Motorbike by plate, or creates a new one under the resolved customer. */
    private int resolveOrCreateMotorbike(Connection conn, String plate, String vehicleInfo, int customerId)
            throws SQLException {
        String selectSql = "SELECT MotorbikeID, CustomerID FROM Motorbikes WHERE LicensePlate = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, plate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int motorbikeId = rs.getInt("MotorbikeID");
                    // If this bike currently has no proper owner link, attach it to the resolved customer
                    return motorbikeId;
                }
            }
        }

        // Parse a loose "Brand Model, Color" style string from the booking form into columns when possible
        String brand = null;
        String model = vehicleInfo;
        if (vehicleInfo != null && vehicleInfo.contains(",")) {
            String[] parts = vehicleInfo.split(",", 2);
            model = parts[0].trim();
        }
        if (model != null && model.contains(" ")) {
            brand = model.split(" ")[0];
        }

        String insertSql = "INSERT INTO Motorbikes (CustomerID, LicensePlate, Brand, Model) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            ps.setString(2, plate);
            ps.setString(3, brand);
            ps.setString(4, model);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to resolve or create motorbike for plate " + plate);
    }

    private int insertRepairOrder(Connection conn, int customerId, int motorbikeId, int bookingId,
                                   Integer assignedStaffId, String description, BigDecimal estimatedCost)
            throws SQLException {
        String status = assignedStaffId != null ? "PENDING" : "PENDING";
        String sql = "INSERT INTO RepairOrders (CustomerID, MotorbikeID, BookingID, AssignedStaffID, Status, "
                + "Description, EstimatedCost, TotalCost, CreatedDate, AssignedDate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 0, GETDATE(), ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            ps.setInt(2, motorbikeId);
            ps.setInt(3, bookingId);
            if (assignedStaffId != null) {
                ps.setInt(4, assignedStaffId);
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, status);
            ps.setString(6, description);
            ps.setBigDecimal(7, estimatedCost);
            if (assignedStaffId != null) {
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
        }
        throw new SQLException("Failed to create repair order from booking #" + bookingId);
    }
}
