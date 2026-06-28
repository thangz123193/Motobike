package controller.staff;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.User;

/**
 * "View employee list (only Staff)" use case from the diagram: Staff can see
 * their fellow technicians (RoleID=2), e.g. to know who else is on shift.
 */
@WebServlet(name = "StaffEmployeeListServlet", urlPatterns = {"/staff/employees"})
public class StaffEmployeeListServlet extends HttpServlet {

    private static final int STAFF_ROLE_ID = 2;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<User> staffList = new UserDAO().getUsersByRole(STAFF_ROLE_ID, null);
        req.setAttribute("staffList", staffList);
        req.getRequestDispatcher("/staff/employees.jsp").forward(req, resp);
    }
}
