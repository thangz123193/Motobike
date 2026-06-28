package controller.staff;

import dao.AuditLogDAO;
import dao.OrderDetailDAO;
import dao.RepairOrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import model.User;
import util.ServletUtils;

/**
 * Handles all RepairOrder workflow transitions for a Staff member, driven by
 * the "action" parameter:
 *   action=accept   -> PENDING    -> ACCEPTED
 *   action=reject   -> PENDING    -> REJECTED   (requires "reason")
 *   action=start    -> ACCEPTED   -> PROCESSING
 *   action=complete -> PROCESSING -> COMPLETED  (stamps TotalCost from OrderDetails sum)
 *
 * Every DAO update already includes "AND AssignedStaffID = ? AND Status = ?"
 * guards, so a staff member cannot transition an order they don't own or
 * skip a workflow step out of order (e.g. complete before start).
 */
@WebServlet(name = "StaffOrderActionServlet", urlPatterns = {"/staff/order-action"})
public class StaffOrderActionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        int orderID = ServletUtils.getParamInt(req, "orderId", -1);
        String action = ServletUtils.getParamTrimmed(req, "action");
        RepairOrderDAO orderDAO = new RepairOrderDAO();
        AuditLogDAO auditDAO = new AuditLogDAO();

        boolean success = false;
        String auditAction = "ORDER_ACTION";
        String auditDetail = "Order #" + orderID + " action=" + action;

        if (action == null) {
            redirectBack(req, resp, orderID, false);
            return;
        }

        switch (action) {
            case "accept":
                success = orderDAO.accept(orderID, user.getUserID());
                auditAction = "ACCEPT_ORDER";
                break;

            case "reject": {
                String reason = ServletUtils.getParamTrimmed(req, "reason");
                if (reason == null) {
                    req.setAttribute("error", "Please enter a reason for declining.");
                    success = false;
                } else {
                    success = orderDAO.reject(orderID, user.getUserID(), reason);
                    auditDetail += " reason=" + reason;
                }
                auditAction = "REJECT_ORDER";
                break;
            }

            case "start":
                success = orderDAO.startProcessing(orderID, user.getUserID());
                auditAction = "START_PROCESSING";
                break;

            case "complete": {
                BigDecimal totalCost = new OrderDetailDAO().sumTotalForOrder(orderID);
                success = orderDAO.complete(orderID, user.getUserID(), totalCost);
                auditAction = "COMPLETE_ORDER";
                auditDetail += " totalCost=" + totalCost;
                break;
            }

            default:
                success = false;
        }

        if (success) {
            auditDAO.log(user.getUserID(), auditAction, auditDetail);
        }

        redirectBack(req, resp, orderID, success);
    }

    private void redirectBack(HttpServletRequest req, HttpServletResponse resp, int orderID, boolean success)
            throws IOException {
        String flag = success ? "success=1" : "error=1";
        resp.sendRedirect(req.getContextPath() + "/staff/order-detail?id=" + orderID + "&" + flag);
    }
}
