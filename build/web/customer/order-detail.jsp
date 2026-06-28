<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Repair order #${order.orderID}</div>
    </div>

    <c:if test="${param.rated == '1'}">
        <div class="alert alert-success">Thanks for your feedback!</div>
    </c:if>
    <c:if test="${param.error == '1'}">
        <div class="alert alert-error">We couldn't submit your feedback. Please check and try again.</div>
    </c:if>

    <div class="grid-2">
        <div class="card">
            <h3>Order details</h3>
            <p><strong>License plate:</strong> ${order.licensePlate}</p>
            <p><strong>Description:</strong> ${order.description}</p>
            <p><strong>Technician:</strong> ${order.assignedStaffName != null ? order.assignedStaffName : 'Not assigned yet'}</p>
            <p><strong>Status:</strong>
                <c:choose>
                    <c:when test="${order.status == 'PENDING'}"><span class="badge badge-pending">Pending</span></c:when>
                    <c:when test="${order.status == 'ACCEPTED'}"><span class="badge badge-accepted">Accepted</span></c:when>
                    <c:when test="${order.status == 'PROCESSING'}"><span class="badge badge-processing">In progress</span></c:when>
                    <c:when test="${order.status == 'COMPLETED'}"><span class="badge badge-completed">Completed</span></c:when>
                    <c:when test="${order.status == 'REJECTED'}"><span class="badge badge-rejected">Declined</span></c:when>
                </c:choose>
            </p>
            <c:if test="${order.status == 'REJECTED'}">
                <p><strong>Reason:</strong> ${order.rejectReason}</p>
            </c:if>
            <p><strong>Created:</strong> <fmt:formatDate value="${order.createdDate}" pattern="MMM d, yyyy h:mm a"/></p>
            <c:if test="${order.completedDate != null}">
                <p><strong>Completed:</strong> <fmt:formatDate value="${order.completedDate}" pattern="MMM d, yyyy h:mm a"/></p>
            </c:if>
        </div>

        <div class="card">
            <h3>Cost breakdown</h3>
            <table>
                <thead><tr><th>Item</th><th>Qty</th><th>Unit price</th><th>Total</th></tr></thead>
                <tbody>
                <c:forEach var="d" items="${details}">
                    <tr>
                        <td>${d.itemName} <span class="text-muted">(${d.itemType == 'PART' ? 'Part' : 'Service'})</span></td>
                        <td>${d.quantity}</td>
                        <td>$<fmt:formatNumber value="${d.unitPrice}" groupingUsed="true"/></td>
                        <td>$<fmt:formatNumber value="${d.lineTotal}" groupingUsed="true"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <c:if test="${empty details}">
                <p class="text-muted">No costs recorded yet.</p>
            </c:if>
            <hr class="divider">
            <p class="text-right" style="font-weight:700; font-size:16px;">
                Total: $<fmt:formatNumber value="${order.totalCost}" groupingUsed="true"/>
            </p>
        </div>
    </div>

    <div class="card">
        <h3>Inspection &amp; repair log</h3>
        <c:choose>
            <c:when test="${empty logs}">
                <p class="text-muted">No notes from the technician yet.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="log" items="${logs}">
                    <p style="margin-bottom:10px;">
                        <strong>${log.staffName}</strong>
                        <span class="text-muted">(<fmt:formatDate value="${log.createdDate}" pattern="MMM d, yyyy h:mm a"/>)</span><br>
                        ${log.note}
                    </p>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${order.status == 'COMPLETED' && !hasFeedback}">
        <div class="card" style="max-width:500px;">
            <h3>Rate this service</h3>
            <form method="post" action="${pageContext.request.contextPath}/customer/feedback">
                <input type="hidden" name="orderId" value="${order.orderID}">
                <label for="rating">Rating (1-5)</label>
                <select id="rating" name="rating" required>
                    <option value="5">5 - Excellent</option>
                    <option value="4">4 - Good</option>
                    <option value="3">3 - Average</option>
                    <option value="2">2 - Poor</option>
                    <option value="1">1 - Very poor</option>
                </select>
                <label for="comment">Comment</label>
                <textarea id="comment" name="comment" rows="3" placeholder="Tell us about your experience..."></textarea>
                <button type="submit" class="btn btn-primary">Submit feedback</button>
            </form>
        </div>
    </c:if>
    <c:if test="${order.status == 'COMPLETED' && hasFeedback}">
        <div class="alert alert-info">You've already rated this order. Thank you!</div>
    </c:if>
</div>

<jsp:include page="/common/footer.jsp" />
