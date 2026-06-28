package controller.customer;

import dao.MotorbikeDAO;
import dao.RepairOrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.Motorbike;
import model.RepairOrder;
import model.User;

/**
 * Customer Dashboard: shows the customer's motorbikes and the live status
 * of their repair orders (PENDING/ACCEPTED/PROCESSING/COMPLETED/REJECTED).
 */
@WebServlet(name = "CustomerDashboardServlet", urlPatterns = {"/customer/dashboard"})
public class CustomerDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        List<RepairOrder> orders = new RepairOrderDAO().getByCustomerID(user.getUserID());
        List<Motorbike> bikes = new MotorbikeDAO().getByCustomerID(user.getUserID());

        req.setAttribute("orders", orders);
        req.setAttribute("bikes", bikes);
        req.getRequestDispatcher("/customer/dashboard.jsp").forward(req, resp);
    }
}
