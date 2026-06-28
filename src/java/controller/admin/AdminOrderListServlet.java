package controller.admin;

import dao.RepairOrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.RepairOrder;
import util.ServletUtils;

/** "Manage repair order" use case (Admin actor): full order list with status filter. */
@WebServlet(name = "AdminOrderListServlet", urlPatterns = {"/admin/orders"})
public class AdminOrderListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String status = ServletUtils.getParamTrimmed(req, "status");
        List<RepairOrder> orders = new RepairOrderDAO().getAll(status);
        req.setAttribute("orders", orders);
        req.setAttribute("statusFilter", status);
        req.getRequestDispatcher("/admin/order-list.jsp").forward(req, resp);
    }
}
