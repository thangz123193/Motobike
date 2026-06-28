package controller.admin;

import dao.OrderDetailDAO;
import dao.RepairOrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.RepairOrder;
import util.ServletUtils;

/**
 * Invoice view for a COMPLETED order: shows the final cost breakdown
 * (parts + services) in a printable layout. Maps to "AdminFormCreate" /
 * the printable invoice screen reachable from Admin Dashboard.
 */
@WebServlet(name = "AdminInvoiceViewServlet", urlPatterns = {"/admin/invoice"})
public class AdminInvoiceViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int orderID = ServletUtils.getParamInt(req, "id", -1);
        RepairOrder order = new RepairOrderDAO().getByID(orderID);

        if (order == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Repair order not found.");
            return;
        }

        req.setAttribute("order", order);
        req.setAttribute("details", new OrderDetailDAO().getByOrderID(orderID));
        req.getRequestDispatcher("/admin/invoice.jsp").forward(req, resp);
    }
}
