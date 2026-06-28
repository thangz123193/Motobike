package controller.admin;

import dao.MotorbikeDAO;
import dao.RepairOrderDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Motorbike;
import model.RepairOrder;
import model.User;
import util.ServletUtils;

/** Admin's customer directory: search/list customers, view one customer's bikes + repair history. */
@WebServlet(name = "AdminCustomerServlet", urlPatterns = {"/admin/customers"})
public class AdminCustomerServlet extends HttpServlet {

    private static final int CUSTOMER_ROLE_ID = 3;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int customerId = ServletUtils.getParamInt(req, "id", -1);

        if (customerId > 0) {
            User customer = new UserDAO().getUserByID(customerId);
            if (customer == null || !customer.isCustomer()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer not found.");
                return;
            }
            List<Motorbike> bikes = new MotorbikeDAO().getByCustomerID(customerId);
            List<RepairOrder> orders = new RepairOrderDAO().getByCustomerID(customerId);
            req.setAttribute("customer", customer);
            req.setAttribute("bikes", bikes);
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/admin/customer-detail.jsp").forward(req, resp);
            return;
        }

        String keyword = ServletUtils.getParamTrimmed(req, "keyword");
        List<User> customers = new UserDAO().getUsersByRole(CUSTOMER_ROLE_ID, keyword);
        req.setAttribute("customers", customers);
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("/admin/customer-list.jsp").forward(req, resp);
    }
}
