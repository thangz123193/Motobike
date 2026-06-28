<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Customer feedback</div>
    </div>

    <div class="stat-box" style="max-width:240px; margin-bottom:20px;">
        <div class="stat-value"><fmt:formatNumber value="${avgRating}" maxFractionDigits="1"/> / 5</div>
        <div class="stat-label">Average rating</div>
    </div>

    <div class="card">
        <c:choose>
            <c:when test="${empty feedbackList}">
                <div class="empty-state">No feedback yet.</div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead><tr><th>Customer</th><th>Order</th><th>Rating</th><th>Comment</th><th>Date</th></tr></thead>
                    <tbody>
                    <c:forEach var="f" items="${feedbackList}">
                        <tr>
                            <td>${f.customerName}</td>
                            <td><a href="${pageContext.request.contextPath}/admin/order-detail?id=${f.orderID}">#${f.orderID}</a></td>
                            <td class="rating-stars">
                                <c:forEach begin="1" end="${f.rating}">★</c:forEach>
                                <c:forEach begin="${f.rating + 1}" end="5">☆</c:forEach>
                            </td>
                            <td>${f.comment}</td>
                            <td><fmt:formatDate value="${f.createdDate}" pattern="MMM d, yyyy"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
