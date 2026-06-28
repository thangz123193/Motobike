package controller.common;

import dao.AuditLogDAO;
import dao.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // If already logged in, skip the login page and go straight to the right dashboard
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            redirectToDashboard(req, resp, (User) session.getAttribute("user"));
            return;
        }
        RequestDispatcher rd = req.getRequestDispatcher("/common/login.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("error", "Please enter both username and password.");
            req.getRequestDispatcher("/common/login.jsp").forward(req, resp);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.checkLogin(username.trim(), password);

        if (user == null) {
            // Distinguish "wrong password" from "account locked" for a clearer message
            User existing = userDAO.getUserByUsername(username.trim());
            if (existing != null && !existing.isActive()) {
                req.setAttribute("error", "Your account has been locked. Please contact an administrator.");
            } else {
                req.setAttribute("error", "Incorrect username or password.");
            }
            req.getRequestDispatcher("/common/login.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("user", user);
        new AuditLogDAO().log(user.getUserID(), "LOGIN", "User logged in: " + user.getUsername());

        String redirect = req.getParameter("redirect");
        if (redirect != null && !redirect.trim().isEmpty() && redirect.startsWith(req.getContextPath())) {
            resp.sendRedirect(redirect);
            return;
        }
        redirectToDashboard(req, resp, user);
    }

    private void redirectToDashboard(HttpServletRequest req, HttpServletResponse resp, User user)
            throws IOException {
        String ctx = req.getContextPath();
        if (user.isAdmin()) {
            resp.sendRedirect(ctx + "/admin/home");
        } else if (user.isStaff()) {
            resp.sendRedirect(ctx + "/staff/home");
        } else {
            resp.sendRedirect(ctx + "/customer/home");
        }
    }
}
