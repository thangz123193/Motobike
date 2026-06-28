package controller.staff;

import dao.AuditLogDAO;
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

/** "Create vehicle inspection report" use case (Staff actor): adds a progress/inspection note to an order. */
@WebServlet(name = "StaffAddLogServlet", urlPatterns = {"/staff/add-log"})
public class StaffAddLogServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        int orderID = ServletUtils.getParamInt(req, "orderId", -1);
        String note = ServletUtils.getParamTrimmed(req, "note");

        RepairOrder order = new RepairOrderDAO().getByID(orderID);
        boolean isOwner = order != null && order.getAssignedStaffID() != null
                && order.getAssignedStaffID() == user.getUserID();

        if (order == null || !isOwner || note == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/order-detail?id=" + orderID + "&error=1");
            return;
        }

        int logId = new RepairLogDAO().insert(orderID, user.getUserID(), note);
        if (logId > 0) {
            new AuditLogDAO().log(user.getUserID(), "ADD_REPAIR_LOG", "Order #" + orderID + " note added");
        }
        String flag = logId > 0 ? "success=1" : "error=1";
        resp.sendRedirect(req.getContextPath() + "/staff/order-detail?id=" + orderID + "&" + flag);
    }
}
