package controller.admin;

import dao.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Feedback;

/** "view customer rate service" use case (Admin actor). */
@WebServlet(name = "AdminFeedbackServlet", urlPatterns = {"/admin/feedback"})
public class AdminFeedbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        FeedbackDAO feedbackDAO = new FeedbackDAO();
        List<Feedback> list = feedbackDAO.getAll();
        req.setAttribute("feedbackList", list);
        req.setAttribute("avgRating", feedbackDAO.getAverageRating());
        req.getRequestDispatcher("/admin/feedback-list.jsp").forward(req, resp);
    }
}
