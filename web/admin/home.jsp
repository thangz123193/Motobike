<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Admin dashboard</div>
    </div>

    <div class="grid-4">
        <div class="stat-box warn">
            <div class="stat-value">${pendingOrders}</div>
            <div class="stat-label">Orders pending</div>
        </div>
        <div class="stat-box">
            <div class="stat-value">${processingOrders}</div>
            <div class="stat-label">Orders in progress</div>
        </div>
        <div class="stat-box success">
            <div class="stat-value">${completedOrders}</div>
            <div class="stat-label">Orders completed</div>
        </div>
        <div class="stat-box danger">
            <div class="stat-value">${rejectedOrders}</div>
            <div class="stat-label">Orders declined</div>
        </div>
    </div>

    <div class="grid-3 mt-16">
        <div class="stat-box warn">
            <div class="stat-value">${pendingBookings}</div>
            <div class="stat-label">Bookings awaiting review</div>
        </div>
        <div class="stat-box success">
            <div class="stat-value">$<fmt:formatNumber value="${totalRevenue}" groupingUsed="true"/></div>
            <div class="stat-label">Total revenue</div>
        </div>
        <div class="stat-box">
            <div class="stat-value"><fmt:formatNumber value="${avgRating}" maxFractionDigits="1"/> / 5</div>
            <div class="stat-label">Average rating</div>
        </div>
    </div>

    <div class="grid-3 mt-16">
        <div class="card">
            <h3>Manage bookings</h3>
            <p class="text-muted">Review and act on customer booking requests.</p>
            <a class="btn btn-primary mt-16" href="${pageContext.request.contextPath}/admin/bookings">View bookings</a>
        </div>
        <div class="card">
            <h3>Walk-in repair order</h3>
            <p class="text-muted">Create a repair order directly for a customer at the shop.</p>
            <a class="btn btn-primary mt-16" href="${pageContext.request.contextPath}/admin/create-order">New order</a>
        </div>
        <div class="card">
            <h3>Manage repair orders</h3>
            <p class="text-muted">Track and assign technicians to repair orders.</p>
            <a class="btn btn-primary mt-16" href="${pageContext.request.contextPath}/admin/orders">View all orders</a>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
