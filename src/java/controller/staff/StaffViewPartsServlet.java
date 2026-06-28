package controller.staff;

import dao.PartDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Part;

/** "View parts of shop" use case (Staff actor). Read-only list. */
@WebServlet(name = "StaffViewPartsServlet", urlPatterns = {"/staff/parts"})
public class StaffViewPartsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Part> parts = new PartDAO().getAll(true);
        req.setAttribute("parts", parts);
        req.getRequestDispatcher("/staff/parts.jsp").forward(req, resp);
    }
}
