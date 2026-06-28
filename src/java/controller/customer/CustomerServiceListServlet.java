package controller.customer;

import dao.ServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Service;

/** "View service" use case (Customer actor). Shows only active services. */
@WebServlet(name = "CustomerServiceListServlet", urlPatterns = {"/customer/services"})
public class CustomerServiceListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Service> services = new ServiceDAO().getAll(true);
        req.setAttribute("services", services);
        req.getRequestDispatcher("/customer/services.jsp").forward(req, resp);
    }
}
