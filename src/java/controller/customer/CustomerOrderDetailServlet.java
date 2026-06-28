package controller.customer;

import dao.FeedbackDAO;
import dao.OrderDetailDAO;
import dao.RepairLogDAO;
import dao.RepairOrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.RepairOrder;
import model.User;
import util.ServletUtils;

/**
 * Customer's detail view of a single repair order: shows parts/services used,
 * staff repair log notes, and (if COMPLETED) a feedback form to rate the service.
 */
@WebServlet(name = "CustomerOrderDetailServlet", urlPatterns = {"/customer/order-detail"})
public class CustomerOrderDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        int orderID = ServletUtils.getParamInt(req, "id", -1);

        RepairOrderDAO orderDAO = new RepairOrderDAO();
        RepairOrder order = orderDAO.getByID(orderID);

        // Ownership check: a customer may only view their own orders
        if (order == null || order.getCustomerID() == null || order.getCustomerID() != user.getUserID()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permission to view this repair order.");
            return;
        }

        req.setAttribute("order", order);
        req.setAttribute("details", new OrderDetailDAO().getByOrderID(orderID));
        req.setAttribute("logs", new RepairLogDAO().getByOrderID(orderID));
        req.setAttribute("hasFeedback", new FeedbackDAO().existsForOrder(orderID));
        req.getRequestDispatcher("/customer/order-detail.jsp").forward(req, resp);
    }
}
