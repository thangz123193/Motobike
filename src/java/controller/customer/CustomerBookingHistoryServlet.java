package controller.customer;

import dao.CustomerBookingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.CustomerBooking;
import model.User;

/**
 * History booking screen: implements "view detail of form they send" use case.
 * Shows the customer all bookings they've submitted and their current status
 * (PENDING / CONFIRMED / REJECTED / CONVERTED).
 */
@WebServlet(name = "CustomerBookingHistoryServlet", urlPatterns = {"/customer/booking-history"})
public class CustomerBookingHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        List<CustomerBooking> bookings = new CustomerBookingDAO().getByCustomerID(user.getUserID());
        req.setAttribute("bookings", bookings);
        req.getRequestDispatcher("/customer/booking-history.jsp").forward(req, resp);
    }
}
