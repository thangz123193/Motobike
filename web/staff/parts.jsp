<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Parts catalog</div>
    </div>
    <div class="card">
        <table>
            <thead><tr><th>Part name</th><th>Description</th><th>Unit</th><th>Price</th><th>In stock</th></tr></thead>
            <tbody>
            <c:forEach var="p" items="${parts}">
                <tr>
                    <td>${p.partName}</td>
                    <td>${p.description}</td>
                    <td>${p.unit}</td>
                    <td>$<fmt:formatNumber value="${p.price}" groupingUsed="true"/></td>
                    <td>${p.quantity}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <c:if test="${empty parts}"><div class="empty-state">No parts available.</div></c:if>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
