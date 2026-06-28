<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Book a repair</div>
    </div>

    <div class="card" style="max-width:600px;">
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/customer/booking-form">
            <label for="licensePlate">License plate *</label>
            <input type="text" id="licensePlate" name="licensePlate" placeholder="e.g. 59-X1 123.45" required>

            <label for="vehicleInfo">Vehicle info (brand, model, color)</label>
            <input type="text" id="vehicleInfo" name="vehicleInfo" placeholder="e.g. Honda Wave Alpha, red">

            <label for="description">Describe the issue *</label>
            <textarea id="description" name="description" rows="4" placeholder="Tell us what's wrong with your bike..." required></textarea>

            <label for="preferredDate">Preferred date &amp; time</label>
            <input type="datetime-local" id="preferredDate" name="preferredDate">

            <button type="submit" class="btn btn-primary">Submit request</button>
        </form>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
