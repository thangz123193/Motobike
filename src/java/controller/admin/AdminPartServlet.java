package controller.admin;

import dao.AuditLogDAO;
import dao.PartDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import model.Part;
import model.User;
import util.ServletUtils;

/** "Manage parts of shop" use case (Admin actor). Same list+create+update+deactivate+restock pattern as services. */
@WebServlet(name = "AdminPartServlet", urlPatterns = {"/admin/parts"})
public class AdminPartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Part> parts = new PartDAO().getAll(false);
        req.setAttribute("parts", parts);
        req.getRequestDispatcher("/admin/part-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User admin = (User) session.getAttribute("user");
        String action = ServletUtils.getParamTrimmed(req, "action");
        PartDAO partDAO = new PartDAO();
        AuditLogDAO auditDAO = new AuditLogDAO();

        if ("create".equals(action)) {
            Part p = new Part();
            p.setPartName(ServletUtils.getParamTrimmed(req, "partName"));
            p.setDescription(ServletUtils.getParamTrimmed(req, "description"));
            p.setUnit(ServletUtils.getParamTrimmed(req, "unit"));
            p.setPrice(ServletUtils.parseDecimalOrDefault(req.getParameter("price"), BigDecimal.ZERO));
            p.setQuantity(ServletUtils.getParamInt(req, "quantity", 0));
            p.setActive(true);
            int id = partDAO.insert(p);
            if (id > 0) {
                auditDAO.log(admin.getUserID(), "CREATE_PART", "Part #" + id + " created: " + p.getPartName());
            }
        } else if ("update".equals(action)) {
            Part p = new Part();
            p.setPartID(ServletUtils.getParamInt(req, "partId", -1));
            p.setPartName(ServletUtils.getParamTrimmed(req, "partName"));
            p.setDescription(ServletUtils.getParamTrimmed(req, "description"));
            p.setUnit(ServletUtils.getParamTrimmed(req, "unit"));
            p.setPrice(ServletUtils.parseDecimalOrDefault(req.getParameter("price"), BigDecimal.ZERO));
            p.setQuantity(ServletUtils.getParamInt(req, "quantity", 0));
            p.setActive("on".equals(req.getParameter("isActive")) || "true".equals(req.getParameter("isActive")));
            partDAO.update(p);
            auditDAO.log(admin.getUserID(), "UPDATE_PART", "Part #" + p.getPartID() + " updated");
        } else if ("restock".equals(action)) {
            int id = ServletUtils.getParamInt(req, "partId", -1);
            int amount = ServletUtils.getParamInt(req, "amount", 0);
            if (amount > 0) {
                partDAO.addStock(id, amount);
                auditDAO.log(admin.getUserID(), "RESTOCK_PART", "Part #" + id + " +" + amount + " units");
            }
        } else if ("deactivate".equals(action)) {
            int id = ServletUtils.getParamInt(req, "partId", -1);
            partDAO.deactivate(id);
            auditDAO.log(admin.getUserID(), "DEACTIVATE_PART", "Part #" + id + " deactivated");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/parts?success=1");
    }
}
