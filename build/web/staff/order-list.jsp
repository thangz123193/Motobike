<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Assigned repair orders</div>
    </div>

    <div class="card">
        <div style="margin-bottom:16px;">
            <a class="btn btn-sm ${empty statusFilter ? 'btn-primary' : 'btn-secondary'}" href="?">All</a>
            <a class="btn btn-sm ${statusFilter == 'PENDING' ? 'btn-primary' : 'btn-secondary'}" href="?status=PENDING">Pending</a>
            <a class="btn btn-sm ${statusFilter == 'ACCEPTED' ? 'btn-primary' : 'btn-secondary'}" href="?status=ACCEPTED">Accepted</a>
            <a class="btn btn-sm ${statusFilter == 'PROCESSING' ? 'btn-primary' : 'btn-secondary'}" href="?status=PROCESSING">In progress</a>
            <a class="btn btn-sm ${statusFilter == 'COMPLETED' ? 'btn-primary' : 'btn-secondary'}" href="?status=COMPLETED">Completed</a>
            <a class="btn btn-sm ${statusFilter == 'REJECTED' ? 'btn-primary' : 'btn-secondary'}" href="?status=REJECTED">Declined</a>
        </div>

        <c:choose>
            <c:when test="${empty orders}">
                <div class="empty-state">No repair orders found.</div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                    <tr><th>Order</th><th>Customer</th><th>Phone</th><th>Plate</th><th>Description</th><th>Status</th><th>Created</th><th></th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="o" items="${orders}">
                        <tr>
                            <td>#${o.orderID}</td>
                            <td>${o.customerName}</td>
                            <td>${o.customerPhone}</td>
                            <td>${o.licensePlate}</td>
                            <td>${o.description}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${o.status == 'PENDING'}"><span class="badge badge-pending">Pending</span></c:when>
                                    <c:when test="${o.status == 'ACCEPTED'}"><span class="badge badge-accepted">Accepted</span></c:when>
                                    <c:when test="${o.status == 'PROCESSING'}"><span class="badge badge-processing">In progress</span></c:when>
                                    <c:when test="${o.status == 'COMPLETED'}"><span class="badge badge-completed">Completed</span></c:when>
                                    <c:when test="${o.status == 'REJECTED'}"><span class="badge badge-rejected">Declined</span></c:when>
                                </c:choose>
                            </td>
                            <td><fmt:formatDate value="${o.createdDate}" pattern="MMM d, yyyy"/></td>
                            <td><a href="${pageContext.request.contextPath}/staff/order-detail?id=${o.orderID}">Details</a></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
