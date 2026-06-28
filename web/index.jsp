<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.User" %>
<%
    User user = (User) session.getAttribute("user");
    String ctx = request.getContextPath();

    if (user == null) {
        response.sendRedirect(ctx + "/login");
    } else if (user.isAdmin()) {
        response.sendRedirect(ctx + "/admin/home");
    } else if (user.isStaff()) {
        response.sendRedirect(ctx + "/staff/home");
    } else {
        response.sendRedirect(ctx + "/customer/home");
    }
%>
