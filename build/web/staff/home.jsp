<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Welcome back, ${sessionScope.user.fullName}</div>
    </div>

    <div class="grid-3">
        <div class="card">
            <h3>My orders</h3>
            <p class="text-muted">View and work through the orders assigned to you.</p>
            <a class="btn btn-primary mt-16" href="${pageContext.request.contextPath}/staff/orders">View my orders</a>
        </div>
        <div class="card">
            <h3>Parts</h3>
            <p class="text-muted">Look up parts and current stock levels.</p>
            <a class="btn btn-secondary mt-16" href="${pageContext.request.contextPath}/staff/parts">View parts</a>
        </div>
        <div class="card">
            <h3>Services</h3>
            <p class="text-muted">Look up the repair services we offer.</p>
            <a class="btn btn-secondary mt-16" href="${pageContext.request.contextPath}/staff/services">View services</a>
        </div>
    </div>

    <div class="card">
        <h3>Team</h3>
        <p class="text-muted">See the other technicians at the shop.</p>
        <a class="btn btn-secondary mt-16" href="${pageContext.request.contextPath}/staff/employees">View team</a>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
