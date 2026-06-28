package controller.admin;

import dao.CustomerBookingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.CustomerBooking;
import util.ServletUtils;

/** Booking List screen: Admin reviews all customer booking requests ("Check booking" use case). */
@WebServlet(name = "AdminBookingListServlet", urlPatterns = {"/admin/bookings"})
public class AdminBookingListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String status = ServletUtils.getParamTrimmed(req, "status");
        List<CustomerBooking> bookings = new CustomerBookingDAO().getAll(status);
        req.setAttribute("bookings", bookings);
        req.setAttribute("statusFilter", status);
        req.getRequestDispatcher("/admin/booking-list.jsp").forward(req, resp);
    }
}
