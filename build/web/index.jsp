<%@ page contentType="text/html;charset=UTF-8" %>
<%
    // The "/" path is handled by RootServlet (annotated @WebServlet urlPatterns={"/"}),
    // but having a literal index.jsp as the welcome-file keeps Tomcat happy if someone
    // requests a static-looking path. Just bounce to the same routing logic.
    response.sendRedirect(request.getContextPath() + "/login");
%>
