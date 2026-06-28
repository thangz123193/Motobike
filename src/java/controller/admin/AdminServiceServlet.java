package controller.admin;

import dao.AuditLogDAO;
import dao.ServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import model.Service;
import model.User;
import util.ServletUtils;

/**
 * "Manage services of shop" use case (Admin actor). Single servlet handling
 * list + create + update + deactivate via an "action" parameter, to keep the
 * Services List screen self-contained.
 */
@WebServlet(name = "AdminServiceServlet", urlPatterns = {"/admin/services"})
public class AdminServiceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Service> services = new ServiceDAO().getAll(false); // admin sees inactive ones too
        req.setAttribute("services", services);
        req.getRequestDispatcher("/admin/service-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User admin = (User) session.getAttribute("user");
        String action = ServletUtils.getParamTrimmed(req, "action");
        ServiceDAO serviceDAO = new ServiceDAO();
        AuditLogDAO auditDAO = new AuditLogDAO();

        if ("create".equals(action)) {
            Service s = new Service();
            s.setServiceName(ServletUtils.getParamTrimmed(req, "serviceName"));
            s.setDescription(ServletUtils.getParamTrimmed(req, "description"));
            s.setPrice(ServletUtils.parseDecimalOrDefault(req.getParameter("price"), BigDecimal.ZERO));
            s.setActive(true);
            int id = serviceDAO.insert(s);
            if (id > 0) {
                auditDAO.log(admin.getUserID(), "CREATE_SERVICE", "Service #" + id + " created: " + s.getServiceName());
            }
        } else if ("update".equals(action)) {
            Service s = new Service();
            s.setServiceID(ServletUtils.getParamInt(req, "serviceId", -1));
            s.setServiceName(ServletUtils.getParamTrimmed(req, "serviceName"));
            s.setDescription(ServletUtils.getParamTrimmed(req, "description"));
            s.setPrice(ServletUtils.parseDecimalOrDefault(req.getParameter("price"), BigDecimal.ZERO));
            s.setActive("on".equals(req.getParameter("isActive")) || "true".equals(req.getParameter("isActive")));
            serviceDAO.update(s);
            auditDAO.log(admin.getUserID(), "UPDATE_SERVICE", "Service #" + s.getServiceID() + " updated");
        } else if ("deactivate".equals(action)) {
            int id = ServletUtils.getParamInt(req, "serviceId", -1);
            serviceDAO.deactivate(id);
            auditDAO.log(admin.getUserID(), "DEACTIVATE_SERVICE", "Service #" + id + " deactivated");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/services?success=1");
    }
}
