<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Repair order #${order.orderID}</div>
    </div>

    <c:if test="${param.success == '1'}"><div class="alert alert-success">Done.</div></c:if>
    <c:if test="${param.error == '1'}"><div class="alert alert-error">Something went wrong.</div></c:if>
    <c:if test="${param.error == 'cannot_delete'}"><div class="alert alert-error">This order can't be deleted because it has related records (costs, logs, feedback).</div></c:if>

    <div class="grid-2">
        <div class="card">
            <h3>Order details</h3>
            <p><strong>Customer:</strong>
                <c:if test="${order.customerID != null}">
                    <a href="${pageContext.request.contextPath}/admin/customers?id=${order.customerID}">${order.customerName}</a>
                </c:if>
                <c:if test="${order.customerID == null}">${order.customerName}</c:if>
                (${order.customerPhone})
            </p>
            <p><strong>License plate:</strong> ${order.licensePlate}</p>
            <p><strong>Issue:</strong> ${order.description}</p>
            <p><strong>Estimated cost:</strong> $<fmt:formatNumber value="${order.estimatedCost}" groupingUsed="true"/></p>
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

            <hr class="divider">
            <h4>Assign technician</h4>
            <form method="post" action="${pageContext.request.contextPath}/admin/assign-order">
                <input type="hidden" name="orderId" value="${order.orderID}">
                <div class="form-row">
                    <div>
                        <select name="staffId" required>
                            <option value="">— Select technician —</option>
                            <c:forEach var="s" items="${staffList}">
                                <option value="${s.userID}" ${order.assignedStaffID == s.userID ? 'selected' : ''}>${s.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div style="flex:0 0 auto;">
                        <button type="submit" class="btn btn-primary">
                            ${order.assignedStaffID != null ? 'Reassign' : 'Assign'}
                        </button>
                    </div>
                </div>
            </form>
            <p class="text-muted">Currently: ${order.assignedStaffName != null ? order.assignedStaffName : 'Not assigned'}</p>

            <c:if test="${order.status == 'COMPLETED'}">
                <a class="btn btn-secondary mt-16" href="${pageContext.request.contextPath}/admin/invoice?id=${order.orderID}" target="_blank">View invoice</a>
            </c:if>

            <hr class="divider">
            <form method="post" action="${pageContext.request.contextPath}/admin/delete-order"
                  onsubmit="return confirm('Delete this repair order? This action cannot be undone.');">
                <input type="hidden" name="orderId" value="${order.orderID}">
                <button type="submit" class="btn btn-danger btn-sm">Delete order</button>
            </form>
        </div>

        <div class="card">
            <h3>Cost breakdown (parts + services)</h3>
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
            <c:if test="${empty details}"><p class="text-muted">No items added yet.</p></c:if>
            <hr class="divider">
            <p class="text-right" style="font-weight:700; font-size:16px;">
                Total: $<fmt:formatNumber value="${order.totalCost}" groupingUsed="true"/>
            </p>
        </div>
    </div>

    <div class="card">
        <h3>Inspection &amp; repair log</h3>
        <c:forEach var="log" items="${logs}">
            <p style="margin-bottom:10px;">
                <strong>${log.staffName}</strong>
                <span class="text-muted">(<fmt:formatDate value="${log.createdDate}" pattern="MMM d, yyyy h:mm a"/>)</span><br>
                ${log.note}
            </p>
        </c:forEach>
        <c:if test="${empty logs}"><p class="text-muted">No notes added yet.</p></c:if>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
