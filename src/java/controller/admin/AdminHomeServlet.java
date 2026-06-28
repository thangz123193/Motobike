package controller.admin;

import dao.CustomerBookingDAO;
import dao.FeedbackDAO;
import dao.RepairOrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name = "AdminHomeServlet", urlPatterns = {"/admin/home"})
public class AdminHomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        RepairOrderDAO orderDAO = new RepairOrderDAO();
        CustomerBookingDAO bookingDAO = new CustomerBookingDAO();
        FeedbackDAO feedbackDAO = new FeedbackDAO();

        req.setAttribute("pendingOrders", orderDAO.countByStatus("PENDING"));
        req.setAttribute("processingOrders", orderDAO.countByStatus("PROCESSING"));
        req.setAttribute("completedOrders", orderDAO.countByStatus("COMPLETED"));
        req.setAttribute("rejectedOrders", orderDAO.countByStatus("REJECTED"));
        req.setAttribute("pendingBookings", bookingDAO.countByStatus("PENDING"));
        BigDecimal totalRevenue = orderDAO.sumRevenue(null, null);
        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("avgRating", feedbackDAO.getAverageRating());

        req.getRequestDispatcher("/admin/home.jsp").forward(req, resp);
    }
}
