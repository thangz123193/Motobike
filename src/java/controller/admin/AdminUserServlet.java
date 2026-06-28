package controller.admin;

import dao.AuditLogDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.User;
import util.ServletUtils;

/**
 * "Manage account" use case (Admin actor): list/search all users, create Staff
 * accounts, lock/unlock accounts, change roles, reset passwords.
 *
 * Two safety guards are enforced here, both expressed as "an admin cannot
 * weaken their own access in a way that could lock the whole team out":
 *   1. Self-lockout guard: an admin cannot deactivate their own account.
 *   2. Self-downgrade guard: an admin cannot demote their own RoleID away
 *      from Admin.
 */
@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users"})
public class AdminUserServlet extends HttpServlet {

    private static final int ADMIN_ROLE_ID = 1;
    private static final int STAFF_ROLE_ID = 2;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int roleFilter = ServletUtils.getParamInt(req, "role", 0);
        String keyword = ServletUtils.getParamTrimmed(req, "keyword");
        List<User> users = new UserDAO().getUsersByRole(roleFilter, keyword);
        req.setAttribute("users", users);
        req.setAttribute("roleFilter", roleFilter);
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("/admin/user-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User admin = (User) session.getAttribute("user");
        String action = ServletUtils.getParamTrimmed(req, "action");
        UserDAO userDAO = new UserDAO();
        AuditLogDAO auditDAO = new AuditLogDAO();

        switch (action == null ? "" : action) {
            case "createStaff": {
                String username = ServletUtils.getParamTrimmed(req, "username");
                String password = req.getParameter("password");
                String fullName = ServletUtils.getParamTrimmed(req, "fullName");
                String phone = ServletUtils.getParamTrimmed(req, "phone");
                String email = ServletUtils.getParamTrimmed(req, "email");

                if (username == null || password == null || password.length() < 6 || fullName == null) {
                    resp.sendRedirect(req.getContextPath() + "/admin/users?error=invalid");
                    return;
                }
                if (userDAO.usernameExists(username)) {
                    resp.sendRedirect(req.getContextPath() + "/admin/users?error=duplicate");
                    return;
                }
                User staff = new User();
                staff.setUsername(username);
                staff.setPassword(password);
                staff.setFullName(fullName);
                staff.setPhone(phone);
                staff.setEmail(email);
                staff.setRoleID(STAFF_ROLE_ID);
                staff.setActive(true);
                int id = userDAO.insertUser(staff);
                if (id > 0) {
                    auditDAO.log(admin.getUserID(), "CREATE_STAFF", "Staff #" + id + " created: " + username);
                }
                break;
            }

            case "toggleActive": {
                int userId = ServletUtils.getParamInt(req, "userId", -1);
                boolean newStatus = "1".equals(req.getParameter("active"));

                // Guard 1: self-lockout
                if (userId == admin.getUserID() && !newStatus) {
                    resp.sendRedirect(req.getContextPath() + "/admin/users?error=self_lockout");
                    return;
                }
                userDAO.updateActiveStatus(userId, newStatus);
                auditDAO.log(admin.getUserID(), "TOGGLE_ACTIVE",
                        "User #" + userId + " set active=" + newStatus);
                break;
            }

            case "changeRole": {
                int userId = ServletUtils.getParamInt(req, "userId", -1);
                int newRoleId = ServletUtils.getParamInt(req, "roleId", -1);

                // Guard 2: self-downgrade
                if (userId == admin.getUserID() && newRoleId != ADMIN_ROLE_ID) {
                    resp.sendRedirect(req.getContextPath() + "/admin/users?error=self_downgrade");
                    return;
                }
                userDAO.updateRole(userId, newRoleId);
                auditDAO.log(admin.getUserID(), "CHANGE_ROLE",
                        "User #" + userId + " role changed to " + newRoleId);
                break;
            }

            case "resetPassword": {
                int userId = ServletUtils.getParamInt(req, "userId", -1);
                String newPassword = req.getParameter("newPassword");
                if (newPassword == null || newPassword.length() < 6) {
                    resp.sendRedirect(req.getContextPath() + "/admin/users?error=invalid");
                    return;
                }
                userDAO.updatePassword(userId, newPassword);
                auditDAO.log(admin.getUserID(), "RESET_PASSWORD", "Password reset for user #" + userId);
                break;
            }

            case "delete": {
                int userId = ServletUtils.getParamInt(req, "userId", -1);
                if (userId == admin.getUserID()) {
                    resp.sendRedirect(req.getContextPath() + "/admin/users?error=self_delete");
                    return;
                }
                boolean ok = userDAO.deleteUser(userId);
                if (ok) {
                    auditDAO.log(admin.getUserID(), "DELETE_USER", "User #" + userId + " deleted");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/users?error=cannot_delete");
                    return;
                }
                break;
            }

            default:
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=1");
                return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/users?success=1");
    }
}
