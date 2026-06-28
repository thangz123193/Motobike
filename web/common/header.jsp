<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:if test="${not empty pageTitle}">${pageTitle} - </c:if>MotorRepair</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<nav class="navbar">
    <a class="brand" href="${pageContext.request.contextPath}/"><span class="dot"></span> MotorRepair</a>
    <div class="nav-links">
        <c:if test="${sessionScope.user != null}">
            <c:choose>
                <c:when test="${sessionScope.user.admin}">
                    <a href="${pageContext.request.contextPath}/admin/home">Dashboard</a>
                    <a href="${pageContext.request.contextPath}/admin/bookings">Bookings</a>
                    <a href="${pageContext.request.contextPath}/admin/orders">Repair orders</a>
                    <a href="${pageContext.request.contextPath}/admin/customers">Customers</a>
                    <a href="${pageContext.request.contextPath}/admin/services">Services</a>
                    <a href="${pageContext.request.contextPath}/admin/parts">Parts</a>
                    <a href="${pageContext.request.contextPath}/admin/users">Accounts</a>
                    <a href="${pageContext.request.contextPath}/admin/feedback">Feedback</a>
                    <a href="${pageContext.request.contextPath}/admin/report">Reports</a>
                </c:when>
                <c:when test="${sessionScope.user.staff}">
                    <a href="${pageContext.request.contextPath}/staff/home">Dashboard</a>
                    <a href="${pageContext.request.contextPath}/staff/orders">My orders</a>
                    <a href="${pageContext.request.contextPath}/staff/parts">Parts</a>
                    <a href="${pageContext.request.contextPath}/staff/services">Services</a>
                    <a href="${pageContext.request.contextPath}/staff/employees">Team</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/customer/home">Home</a>
                    <a href="${pageContext.request.contextPath}/customer/booking-form">Book a repair</a>
                    <a href="${pageContext.request.contextPath}/customer/booking-history">My bookings</a>
                    <a href="${pageContext.request.contextPath}/customer/dashboard">Repair orders</a>
                    <a href="${pageContext.request.contextPath}/customer/services">Services</a>
                </c:otherwise>
            </c:choose>
            <span class="user-info">${sessionScope.user.fullName}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Sign out</a>
        </c:if>
    </div>
</nav>
