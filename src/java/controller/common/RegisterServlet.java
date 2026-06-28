package controller.common;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;
import util.ServletUtils;

/**
 * Customer self-registration. Admin and Staff accounts are created by Admin only
 * (see AdminUserServlet), never through public registration.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private static final int CUSTOMER_ROLE_ID = 3;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/common/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = ServletUtils.getParamTrimmed(req, "username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String fullName = ServletUtils.getParamTrimmed(req, "fullName");
        String email = ServletUtils.getParamTrimmed(req, "email");
        String phone = ServletUtils.getParamTrimmed(req, "phone");
        String address = ServletUtils.getParamTrimmed(req, "address");

        String error = validate(username, password, confirmPassword, fullName, phone);
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("username", username);
            req.setAttribute("fullName", fullName);
            req.setAttribute("email", email);
            req.setAttribute("phone", phone);
            req.setAttribute("address", address);
            req.getRequestDispatcher("/common/register.jsp").forward(req, resp);
            return;
        }

        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPhone(phone);
        u.setAddress(address);
        u.setRoleID(CUSTOMER_ROLE_ID);
        u.setActive(true);

        int newId = new UserDAO().insertUser(u);
        if (newId > 0) {
            resp.sendRedirect(req.getContextPath() + "/login?registered=1");
        } else {
            req.setAttribute("error", "Registration failed. Please try again.");
            req.getRequestDispatcher("/common/register.jsp").forward(req, resp);
        }
    }

    private String validate(String username, String password, String confirmPassword,
                             String fullName, String phone) {
        if (username == null || username.length() < 4) {
            return "Username must be at least 4 characters long.";
        }
        if (password == null || password.length() < 6) {
            return "Password must be at least 6 characters long.";
        }
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        if (fullName == null) {
            return "Please enter your full name.";
        }
        if (phone == null || !phone.matches("\\d{9,11}")) {
            return "Invalid phone number (9-11 digits).";
        }
        if (new UserDAO().usernameExists(username)) {
            return "This username is already taken. Please choose another.";
        }
        return null;
    }
}
