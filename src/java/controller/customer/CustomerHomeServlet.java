package controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Home Page for logged-in Customers. Matches the "Home Page" box in the
 * screens-flow diagram, which links to: Booking Form, Customer Dashboard,
 * History booking, view Services List, Logout.
 */
@WebServlet(name = "CustomerHomeServlet", urlPatterns = {"/customer/home"})
public class CustomerHomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/customer/home.jsp").forward(req, resp);
    }
}
