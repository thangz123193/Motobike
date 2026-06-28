package filter;

import dao.UserDAO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

/**
 * AuthFilter enforces role-based access control:
 *   - Admin   (RoleID=1) -> can access /admin/* AND /staff/*
 *   - Staff   (RoleID=2) -> can access /staff/* only
 *   - Customer(RoleID=3) -> can access /customer/* only
 *
 * It also re-checks IsActive on every request, so an account that gets
 * locked by an Admin mid-session is kicked out immediately instead of
 * waiting for the session to expire.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/admin/*", "/staff/*", "/customer/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            // Not logged in at all -> bounce to login with a redirect target
            resp.sendRedirect(contextPath + "/login?redirect=" + req.getRequestURI());
            return;
        }

        // Re-verify the account hasn't been locked since login (mid-session lockout)
        boolean stillActive = new UserDAO().isUserActive(user.getUserID());
        if (!stillActive) {
            session.invalidate();
            resp.sendRedirect(contextPath + "/login?error=locked");
            return;
        }

        boolean allowed = false;
        if (uri.contains(contextPath + "/admin/")) {
            allowed = user.isAdmin();
        } else if (uri.contains(contextPath + "/staff/")) {
            allowed = user.isAdmin() || user.isStaff();
        } else if (uri.contains(contextPath + "/customer/")) {
            allowed = user.isCustomer();
        }

        if (!allowed) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this page.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
