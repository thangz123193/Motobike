<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Customer: ${customer.fullName}</div>
    </div>

    <div class="grid-2">
        <div class="card">
            <h3>Contact info</h3>
            <p><strong>Phone:</strong> ${customer.phone}</p>
            <p><strong>Email:</strong> ${customer.email}</p>
            <p><strong>Address:</strong> ${customer.address}</p>
        </div>
        <div class="card">
            <h3>Motorbikes</h3>
            <c:choose>
                <c:when test="${empty bikes}"><p class="text-muted">No motorbikes on file.</p></c:when>
                <c:otherwise>
                    <table>
                        <thead><tr><th>Plate</th><th>Brand</th><th>Model</th></tr></thead>
                        <tbody>
                        <c:forEach var="bike" items="${bikes}">
                            <tr><td>${bike.licensePlate}</td><td>${bike.brand}</td><td>${bike.model}</td></tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="card">
        <h3>Repair history</h3>
        <c:choose>
            <c:when test="${empty orders}"><div class="empty-state">No repair orders yet.</div></c:when>
            <c:otherwise>
                <table>
                    <thead><tr><th>ID</th><th>Plate</th><th>Status</th><th>Total</th><th>Created</th><th></th></tr></thead>
                    <tbody>
                    <c:forEach var="o" items="${orders}">
                        <tr>
                            <td>#${o.orderID}</td>
                            <td>${o.licensePlate}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${o.status == 'PENDING'}"><span class="badge badge-pending">Pending</span></c:when>
                                    <c:when test="${o.status == 'ACCEPTED'}"><span class="badge badge-accepted">Accepted</span></c:when>
                                    <c:when test="${o.status == 'PROCESSING'}"><span class="badge badge-processing">In progress</span></c:when>
                                    <c:when test="${o.status == 'COMPLETED'}"><span class="badge badge-completed">Completed</span></c:when>
                                    <c:when test="${o.status == 'REJECTED'}"><span class="badge badge-rejected">Declined</span></c:when>
                                </c:choose>
                            </td>
                            <td>$<fmt:formatNumber value="${o.totalCost}" groupingUsed="true"/></td>
                            <td><fmt:formatDate value="${o.createdDate}" pattern="MMM d, yyyy"/></td>
                            <td><a href="${pageContext.request.contextPath}/admin/order-detail?id=${o.orderID}">Details</a></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
