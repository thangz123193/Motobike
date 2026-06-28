package controller.customer;

import dao.AuditLogDAO;
import dao.CustomerBookingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import model.CustomerBooking;
import model.User;
import util.ServletUtils;

/**
 * Booking Form screen: implements the "Send form to admin" use case
 * (Customer actor in the use case diagram).
 */
@WebServlet(name = "CustomerBookingFormServlet", urlPatterns = {"/customer/booking-form"})
public class CustomerBookingFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/customer/booking-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        String licensePlate = ServletUtils.getParamTrimmed(req, "licensePlate");
        String vehicleInfo = ServletUtils.getParamTrimmed(req, "vehicleInfo");
        String description = ServletUtils.getParamTrimmed(req, "description");
        String preferredDateStr = ServletUtils.getParamTrimmed(req, "preferredDate");

        if (licensePlate == null || description == null) {
            req.setAttribute("error", "Please enter the license plate and describe the issue.");
            req.getRequestDispatcher("/customer/booking-form.jsp").forward(req, resp);
            return;
        }

        Date preferredDate = null;
        if (preferredDateStr != null) {
            try {
                // HTML <input type="datetime-local"> sends yyyy-MM-dd'T'HH:mm
                preferredDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(preferredDateStr);
            } catch (ParseException ex) {
                preferredDate = null;
            }
        }

        CustomerBooking booking = new CustomerBooking();
        booking.setCustomerID(user.getUserID());
        booking.setFullName(user.getFullName());
        booking.setPhone(user.getPhone());
        booking.setLicensePlate(licensePlate);
        booking.setVehicleInfo(vehicleInfo);
        booking.setDescription(description);
        booking.setPreferredDate(preferredDate);

        int newId = new CustomerBookingDAO().insert(booking);
        if (newId > 0) {
            new AuditLogDAO().log(user.getUserID(), "CREATE_BOOKING", "Booking #" + newId + " created by customer");
            resp.sendRedirect(req.getContextPath() + "/customer/booking-history?success=1");
        } else {
            req.setAttribute("error", "Something went wrong. Please try again.");
            req.getRequestDispatcher("/customer/booking-form.jsp").forward(req, resp);
        }
    }
}
