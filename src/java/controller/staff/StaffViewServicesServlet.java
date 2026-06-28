package controller.staff;

import dao.ServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Service;

/** "View service of shop" use case (Staff actor). Read-only list. */
@WebServlet(name = "StaffViewServicesServlet", urlPatterns = {"/staff/services"})
public class StaffViewServicesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Service> services = new ServiceDAO().getAll(true);
        req.setAttribute("services", services);
        req.getRequestDispatcher("/staff/services.jsp").forward(req, resp);
    }
}
