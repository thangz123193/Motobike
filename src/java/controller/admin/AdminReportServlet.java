package controller.admin;

import dao.RepairOrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import util.ServletUtils;

/** "View financial report" use case (Admin actor): revenue summary with optional date range filter. */
@WebServlet(name = "AdminReportServlet", urlPatterns = {"/admin/report"})
public class AdminReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String fromStr = ServletUtils.getParamTrimmed(req, "from");
        String toStr = ServletUtils.getParamTrimmed(req, "to");

        Date from = parseDate(fromStr);
        Date to = parseDate(toStr);

        RepairOrderDAO orderDAO = new RepairOrderDAO();
        BigDecimal revenue = orderDAO.sumRevenue(from, to);

        req.setAttribute("revenue", revenue);
        req.setAttribute("completedCount", orderDAO.countByStatus("COMPLETED"));
        req.setAttribute("rejectedCount", orderDAO.countByStatus("REJECTED"));
        req.setAttribute("pendingCount", orderDAO.countByStatus("PENDING"));
        req.setAttribute("processingCount", orderDAO.countByStatus("PROCESSING"));
        req.setAttribute("from", fromStr);
        req.setAttribute("to", toStr);

        req.getRequestDispatcher("/admin/report.jsp").forward(req, resp);
    }

    private Date parseDate(String value) {
        if (value == null) {
            return null;
        }
        try {
            java.util.Date d = new SimpleDateFormat("yyyy-MM-dd").parse(value);
            return new Date(d.getTime());
        } catch (ParseException ex) {
            return null;
        }
    }
}
