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
 * "Delete repair order" use case - an <<Extend>> of "Manage repair order" in
 * the diagram. Kept as a separate endpoint to mirror that extension-point
 * relationship: deleting is an exceptional action layered on top of normal
 * order management, not part of the everyday workflow.
 */
@WebServlet(name = "AdminDeleteOrderServlet", urlPatterns = {"/admin/delete-order"})
public class AdminDeleteOrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User admin = (User) session.getAttribute("user");
        int orderID = ServletUtils.getParamInt(req, "orderId", -1);

        boolean success = new RepairOrderDAO().delete(orderID);
        if (success) {
            new AuditLogDAO().log(admin.getUserID(), "DELETE_ORDER", "Order #" + orderID + " deleted");
            resp.sendRedirect(req.getContextPath() + "/admin/orders?success=1");
        } else {
            // Most likely blocked by FK (existing OrderDetails/RepairLogs/Feedback rows)
            resp.sendRedirect(req.getContextPath() + "/admin/order-detail?id=" + orderID
                    + "&error=cannot_delete");
        }
    }
}
