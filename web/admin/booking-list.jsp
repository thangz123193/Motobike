<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Bookings</div>
    </div>

    <c:if test="${param.success == '1'}"><div class="alert alert-success">Done.</div></c:if>
    <c:if test="${param.error == '1'}"><div class="alert alert-error">Something went wrong.</div></c:if>

    <div class="card">
        <div style="margin-bottom:16px;">
            <a class="btn btn-sm ${empty statusFilter ? 'btn-primary' : 'btn-secondary'}" href="?">All</a>
            <a class="btn btn-sm ${statusFilter == 'PENDING' ? 'btn-primary' : 'btn-secondary'}" href="?status=PENDING">Pending</a>
            <a class="btn btn-sm ${statusFilter == 'CONVERTED' ? 'btn-primary' : 'btn-secondary'}" href="?status=CONVERTED">Order created</a>
            <a class="btn btn-sm ${statusFilter == 'REJECTED' ? 'btn-primary' : 'btn-secondary'}" href="?status=REJECTED">Declined</a>
        </div>

        <c:choose>
            <c:when test="${empty bookings}">
                <div class="empty-state">No bookings found.</div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                    <tr><th>ID</th><th>Customer</th><th>Phone</th><th>Plate</th><th>Description</th><th>Preferred date</th><th>Status</th><th>Action</th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="b" items="${bookings}">
                        <tr>
                            <td>#${b.bookingID}</td>
                            <td>${b.fullName}</td>
                            <td>${b.phone}</td>
                            <td>${b.licensePlate}</td>
                            <td>${b.description}</td>
                            <td><fmt:formatDate value="${b.preferredDate}" pattern="MMM d, yyyy h:mm a"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.status == 'PENDING'}"><span class="badge badge-pending">Pending</span></c:when>
                                    <c:when test="${b.status == 'REJECTED'}"><span class="badge badge-rejected">Declined</span></c:when>
                                    <c:when test="${b.status == 'CONVERTED'}"><span class="badge badge-converted">Order created</span></c:when>
                                </c:choose>
                            </td>
                            <td>
                                <c:if test="${b.status == 'PENDING'}">
                                    <button class="btn btn-sm btn-success" onclick="document.getElementById('confirmBox${b.bookingID}').style.display='block'">Confirm</button>
                                    <button class="btn btn-sm btn-danger" onclick="document.getElementById('rejectBox${b.bookingID}').style.display='block'">Decline</button>
                                    <div id="confirmBox${b.bookingID}" style="display:none; margin-top:10px; min-width:260px;">
                                        <form method="post" action="${pageContext.request.contextPath}/admin/booking-action">
                                            <input type="hidden" name="bookingId" value="${b.bookingID}">
                                            <input type="hidden" name="action" value="confirm">
                                            <label>Estimated cost</label>
                                            <input type="number" name="estimatedCost" value="0" min="0">
                                            <button type="submit" class="btn btn-sm btn-success">Create repair order</button>
                                        </form>
                                    </div>
                                    <div id="rejectBox${b.bookingID}" style="display:none; margin-top:10px; min-width:260px;">
                                        <form method="post" action="${pageContext.request.contextPath}/admin/booking-action">
                                            <input type="hidden" name="bookingId" value="${b.bookingID}">
                                            <input type="hidden" name="action" value="reject">
                                            <label>Reason for declining</label>
                                            <textarea name="reason" rows="2" required></textarea>
                                            <button type="submit" class="btn btn-sm btn-danger">Confirm decline</button>
                                        </form>
                                    </div>
                                </c:if>
                                <c:if test="${b.status == 'CONVERTED' && b.repairOrderID != null}">
                                    <a href="${pageContext.request.contextPath}/admin/order-detail?id=${b.repairOrderID}">View order #${b.repairOrderID}</a>
                                </c:if>
                                <c:if test="${b.status == 'REJECTED'}">
                                    <span class="text-muted">${b.rejectReason}</span>
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
