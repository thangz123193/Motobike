package controller.admin;

import dao.AuditLogDAO;
import dao.RepairOrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;
import util.ServletUtils;

/**
 * "Assign order to staff" use case (Admin actor). Also doubles as the
 * reassignment path when an order was REJECTED by one technician and needs
 * to go to someone else - assignStaff() resets the order back to PENDING
 * for the newly assigned staff to accept.
 */
@WebServlet(name = "AdminReassignServlet", urlPatterns = {"/admin/assign-order"})
public class AdminReassignServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User admin = (User) session.getAttribute("user");

        int orderID = ServletUtils.getParamInt(req, "orderId", -1);
        int staffID = ServletUtils.getParamInt(req, "staffId", -1);

        if (orderID <= 0 || staffID <= 0) {
            resp.sendRedirect(req.getContextPath() + "/admin/order-detail?id=" + orderID + "&error=1");
            return;
        }

        boolean success = new RepairOrderDAO().assignStaff(orderID, staffID);
        if (success) {
            new AuditLogDAO().log(admin.getUserID(), "ASSIGN_ORDER",
                    "Order #" + orderID + " assigned to staff #" + staffID);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/order-detail?id=" + orderID
                + "&" + (success ? "success=1" : "error=1"));
    }
}
