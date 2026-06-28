package controller.staff;

import dao.RepairOrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.RepairOrder;
import model.User;
import util.ServletUtils;

/**
 * Staff Order List: shows only orders assigned to the logged-in staff member,
 * with an optional status filter (PENDING/ACCEPTED/PROCESSING/COMPLETED/REJECTED).
 */
@WebServlet(name = "StaffOrderListServlet", urlPatterns = {"/staff/orders"})
public class StaffOrderListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        String status = ServletUtils.getParamTrimmed(req, "status");

        List<RepairOrder> orders = new RepairOrderDAO().getByStaffID(user.getUserID(), status);
        req.setAttribute("orders", orders);
        req.setAttribute("statusFilter", status);
        req.getRequestDispatcher("/staff/order-list.jsp").forward(req, resp);
    }
}
