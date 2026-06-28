package controller.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

@WebServlet(name = "RootServlet", urlPatterns = {"/home"})
public class RootServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        String ctx = req.getContextPath();

        if (user == null) {
            resp.sendRedirect(ctx + "/login");
        } else if (user.isAdmin()) {
            resp.sendRedirect(ctx + "/admin/home");
        } else if (user.isStaff()) {
            resp.sendRedirect(ctx + "/staff/home");
        } else {
            resp.sendRedirect(ctx + "/customer/home");
        }
    }
}
