package controller.admin;

import dao.AuditLogDAO;
import dao.MotorbikeDAO;
import dao.RepairOrderDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import model.Motorbike;
import model.RepairOrder;
import model.User;
import util.ServletUtils;

/**
 * "Create ticket for walk-in customers" use case (Admin actor): a simpler,
 * direct path than the booking conversion flow, for customers physically at
 * the shop with no prior booking. Still resolves/creates Customer + Motorbike
 * as needed using the same lookup logic, but does not involve a BookingID.
 */
@WebServlet(name = "AdminCreateOrderServlet", urlPatterns = {"/admin/create-order"})
public class AdminCreateOrderServlet extends HttpServlet {

    private static final int CUSTOMER_ROLE_ID = 3;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("staffList", new UserDAO().getUsersByRole(2, null));
        req.getRequestDispatcher("/admin/create-order.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User admin = (User) session.getAttribute("user");

        String fullName = ServletUtils.getParamTrimmed(req, "fullName");
        String phone = ServletUtils.getParamTrimmed(req, "phone");
        String licensePlate = ServletUtils.getParamTrimmed(req, "licensePlate");
        String brand = ServletUtils.getParamTrimmed(req, "brand");
        String modelName = ServletUtils.getParamTrimmed(req, "model");
        String description = ServletUtils.getParamTrimmed(req, "description");
        BigDecimal estimatedCost = ServletUtils.parseDecimalOrDefault(req.getParameter("estimatedCost"), BigDecimal.ZERO);
        int assignedStaffId = ServletUtils.getParamInt(req, "assignedStaffId", -1);

        if (fullName == null || phone == null || licensePlate == null) {
            req.setAttribute("error", "Please enter the customer name, phone number, and license plate.");
            req.setAttribute("staffList", new UserDAO().getUsersByRole(2, null));
            req.getRequestDispatcher("/admin/create-order.jsp").forward(req, resp);
            return;
        }

        UserDAO userDAO = new UserDAO();
        MotorbikeDAO bikeDAO = new MotorbikeDAO();

        User customer = userDAO.getUserByPhone(phone);
        int customerId;
        if (customer != null) {
            customerId = customer.getUserID();
        } else {
            User newCustomer = new User();
            newCustomer.setUsername(phone);
            newCustomer.setPassword(phone); // temp password = phone, customer should change later
            newCustomer.setFullName(fullName);
            newCustomer.setPhone(phone);
            newCustomer.setRoleID(CUSTOMER_ROLE_ID);
            newCustomer.setActive(true);
            customerId = userDAO.insertUser(newCustomer);
        }

        Motorbike bike = bikeDAO.getByPlate(licensePlate);
        int motorbikeId;
        if (bike != null) {
            motorbikeId = bike.getMotorbikeID();
        } else {
            Motorbike newBike = new Motorbike();
            newBike.setCustomerID(customerId);
            newBike.setLicensePlate(licensePlate);
            newBike.setBrand(brand);
            newBike.setModel(modelName);
            motorbikeId = bikeDAO.insert(newBike);
        }

        RepairOrder order = new RepairOrder();
        order.setCustomerID(customerId);
        order.setMotorbikeID(motorbikeId);
        order.setDescription(description);
        order.setEstimatedCost(estimatedCost);
        order.setStatus("PENDING");
        if (assignedStaffId > 0) {
            order.setAssignedStaffID(assignedStaffId);
        }

        try {
            int orderId = new RepairOrderDAO().insert(null, order);
            if (orderId > 0) {
                new AuditLogDAO().log(admin.getUserID(), "CREATE_WALKIN_ORDER",
                        "Order #" + orderId + " created for walk-in customer " + fullName);
                resp.sendRedirect(req.getContextPath() + "/admin/order-detail?id=" + orderId + "&success=1");
            } else {
                req.setAttribute("error", "Could not create the repair order. Please try again.");
                req.setAttribute("staffList", new UserDAO().getUsersByRole(2, null));
                req.getRequestDispatcher("/admin/create-order.jsp").forward(req, resp);
            }
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            req.setAttribute("error", "An error occurred while creating the repair order.");
            req.setAttribute("staffList", new UserDAO().getUsersByRole(2, null));
            req.getRequestDispatcher("/admin/create-order.jsp").forward(req, resp);
        }
    }
}
