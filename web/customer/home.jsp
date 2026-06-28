<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div>
            <div class="page-title">Welcome back, ${sessionScope.user.fullName}</div>
            <div class="page-subtitle">Here's what you can do today.</div>
        </div>
    </div>

    <div class="grid-3">
        <div class="card">
            <h3>Book a repair</h3>
            <p class="text-muted">Send a repair request and pick a time that works for you.</p>
            <a class="btn btn-primary mt-16" href="${pageContext.request.contextPath}/customer/booking-form">Book now</a>
        </div>
        <div class="card">
            <h3>My bookings</h3>
            <p class="text-muted">Track the status of requests you've submitted.</p>
            <a class="btn btn-secondary mt-16" href="${pageContext.request.contextPath}/customer/booking-history">View bookings</a>
        </div>
        <div class="card">
            <h3>Repair orders</h3>
            <p class="text-muted">Follow your motorbike's repair progress in real time.</p>
            <a class="btn btn-secondary mt-16" href="${pageContext.request.contextPath}/customer/dashboard">View orders</a>
        </div>
    </div>

    <div class="card">
        <h3>Our services</h3>
        <p class="text-muted">Browse the repair and maintenance services we offer.</p>
        <a class="btn btn-secondary mt-16" href="${pageContext.request.contextPath}/customer/services">View services</a>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
