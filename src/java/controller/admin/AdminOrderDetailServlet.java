package controller.admin;

import dao.OrderDetailDAO;
import dao.RepairLogDAO;
import dao.RepairOrderDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.RepairOrder;
import model.User;
import util.ServletUtils;

/** Admin's detail view of any order: full visibility, plus reassignment and delete actions. */
@WebServlet(name = "AdminOrderDetailServlet", urlPatterns = {"/admin/order-detail"})
public class AdminOrderDetailServlet extends HttpServlet {

    private static final int STAFF_ROLE_ID = 2;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int orderID = ServletUtils.getParamInt(req, "id", -1);
        RepairOrder order = new RepairOrderDAO().getByID(orderID);

        if (order == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Repair order not found.");
            return;
        }

        List<User> staffList = new UserDAO().getUsersByRole(STAFF_ROLE_ID, null);

        req.setAttribute("order", order);
        req.setAttribute("details", new OrderDetailDAO().getByOrderID(orderID));
        req.setAttribute("logs", new RepairLogDAO().getByOrderID(orderID));
        req.setAttribute("staffList", staffList);
        req.getRequestDispatcher("/admin/order-detail.jsp").forward(req, resp);
    }
}
