<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Services catalog</div>
    </div>
    <div class="card">
        <table>
            <thead><tr><th>Service name</th><th>Description</th><th>Price</th></tr></thead>
            <tbody>
            <c:forEach var="s" items="${services}">
                <tr>
                    <td>${s.serviceName}</td>
                    <td>${s.description}</td>
                    <td>$<fmt:formatNumber value="${s.price}" groupingUsed="true"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <c:if test="${empty services}"><div class="empty-state">No services available.</div></c:if>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
