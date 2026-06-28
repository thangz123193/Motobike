package controller.staff;

import dao.OrderDetailDAO;
import dao.PartDAO;
import dao.RepairLogDAO;
import dao.RepairOrderDAO;
import dao.ServiceDAO;
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
 * Staff's detail view of one assigned order: shows the workflow status,
 * parts/services already added, repair log notes, and the action buttons
 * (Accept / Reject / Start / Complete) appropriate to the current status.
 */
@WebServlet(name = "StaffOrderDetailServlet", urlPatterns = {"/staff/order-detail"})
public class StaffOrderDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        int orderID = ServletUtils.getParamInt(req, "id", -1);

        RepairOrder order = new RepairOrderDAO().getByID(orderID);

        // Ownership check: a staff member may only view orders assigned to them
        boolean isOwner = order != null && order.getAssignedStaffID() != null
                && order.getAssignedStaffID() == user.getUserID();
        if (order == null || (!isOwner && !user.isAdmin())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permission to view this repair order.");
            return;
        }

        req.setAttribute("order", order);
        req.setAttribute("details", new OrderDetailDAO().getByOrderID(orderID));
        req.setAttribute("logs", new RepairLogDAO().getByOrderID(orderID));
        req.setAttribute("services", new ServiceDAO().getAll(true));
        req.setAttribute("parts", new PartDAO().getAll(true));
        req.getRequestDispatcher("/staff/order-detail.jsp").forward(req, resp);
    }
}
