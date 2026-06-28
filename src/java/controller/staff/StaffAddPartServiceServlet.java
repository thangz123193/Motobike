package controller.staff;

import dao.AuditLogDAO;
import dao.OrderDetailDAO;
import dao.PartDAO;
import dao.RepairOrderDAO;
import dao.ServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.Part;
import model.RepairOrder;
import model.Service;
import model.User;
import util.ServletUtils;

/**
 * Staff adds a service performed or a part used to an order's bill
 * (feeds into "vehicle inspection report" / order detail breakdown).
 * Only allowed while the order is in ACCEPTED or PROCESSING status.
 * Part stock is deducted atomically inside OrderDetailDAO.addPartLine.
 */
@WebServlet(name = "StaffAddPartServiceServlet", urlPatterns = {"/staff/add-item"})
public class StaffAddPartServiceServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        int orderID = ServletUtils.getParamInt(req, "orderId", -1);
        String itemType = ServletUtils.getParamTrimmed(req, "itemType"); // SERVICE or PART
        int itemId = ServletUtils.getParamInt(req, "itemId", -1);
        int quantity = ServletUtils.getParamInt(req, "quantity", 1);

        RepairOrderDAO orderDAO = new RepairOrderDAO();
        RepairOrder order = orderDAO.getByID(orderID);

        boolean isOwner = order != null && order.getAssignedStaffID() != null
                && order.getAssignedStaffID() == user.getUserID();
        boolean validState = order != null
                && ("ACCEPTED".equals(order.getStatus()) || "PROCESSING".equals(order.getStatus()));

        if (order == null || !isOwner || !validState || quantity < 1) {
            resp.sendRedirect(req.getContextPath() + "/staff/order-detail?id=" + orderID + "&error=1");
            return;
        }

        OrderDetailDAO detailDAO = new OrderDetailDAO();
        AuditLogDAO auditDAO = new AuditLogDAO();
        boolean success;

        if ("SERVICE".equals(itemType)) {
            Service service = new ServiceDAO().getByID(itemId);
            if (service == null) {
                resp.sendRedirect(req.getContextPath() + "/staff/order-detail?id=" + orderID + "&error=1");
                return;
            }
            int newId = detailDAO.addServiceLine(orderID, itemId, quantity, service.getPrice());
            success = newId > 0;
            if (success) {
                auditDAO.log(user.getUserID(), "ADD_SERVICE_LINE",
                        "Order #" + orderID + " service=" + service.getServiceName() + " qty=" + quantity);
            }
        } else if ("PART".equals(itemType)) {
            Part part = new PartDAO().getByID(itemId);
            if (part == null) {
                resp.sendRedirect(req.getContextPath() + "/staff/order-detail?id=" + orderID + "&error=1");
                return;
            }
            int result = detailDAO.addPartLine(orderID, itemId, quantity, part.getPrice());
            if (result == -1) {
                // Not enough stock
                resp.sendRedirect(req.getContextPath() + "/staff/order-detail?id=" + orderID + "&error=stock");
                return;
            }
            success = result > 0;
            if (success) {
                auditDAO.log(user.getUserID(), "ADD_PART_LINE",
                        "Order #" + orderID + " part=" + part.getPartName() + " qty=" + quantity);
            }
        } else {
            success = false;
        }

        String flag = success ? "success=1" : "error=1";
        resp.sendRedirect(req.getContextPath() + "/staff/order-detail?id=" + orderID + "&" + flag);
    }
}
