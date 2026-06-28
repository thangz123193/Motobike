<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">My bookings</div>
    </div>

    <c:if test="${param.success == '1'}">
        <div class="alert alert-success">Your booking request has been submitted. We'll confirm it shortly.</div>
    </c:if>

    <div class="card">
        <c:choose>
            <c:when test="${empty bookings}">
                <div class="empty-state">You don't have any bookings yet. <a href="${pageContext.request.contextPath}/customer/booking-form">Book a repair</a></div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>License plate</th>
                        <th>Description</th>
                        <th>Preferred date</th>
                        <th>Status</th>
                        <th>Note</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="b" items="${bookings}">
                        <tr>
                            <td>#${b.bookingID}</td>
                            <td>${b.licensePlate}</td>
                            <td>${b.description}</td>
                            <td><fmt:formatDate value="${b.preferredDate}" pattern="MMM d, yyyy h:mm a"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.status == 'PENDING'}"><span class="badge badge-pending">Pending</span></c:when>
                                    <c:when test="${b.status == 'CONFIRMED'}"><span class="badge badge-confirmed">Confirmed</span></c:when>
                                    <c:when test="${b.status == 'REJECTED'}"><span class="badge badge-rejected">Declined</span></c:when>
                                    <c:when test="${b.status == 'CONVERTED'}"><span class="badge badge-converted">Order created</span></c:when>
                                </c:choose>
                            </td>
                            <td>
                                <c:if test="${b.status == 'REJECTED'}">${b.rejectReason}</c:if>
                                <c:if test="${b.status == 'CONVERTED' && b.repairOrderID != null}">
                                    <a href="${pageContext.request.contextPath}/customer/order-detail?id=${b.repairOrderID}">View order #${b.repairOrderID}</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
