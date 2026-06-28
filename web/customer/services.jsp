<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Repair &amp; maintenance services</div>
    </div>

    <div class="grid-3">
        <c:forEach var="s" items="${services}">
            <div class="card">
                <h3>${s.serviceName}</h3>
                <p class="text-muted">${s.description}</p>
                <p style="font-weight:700; color:var(--color-accent); margin-top:10px; font-size:15px;">
                    $<fmt:formatNumber value="${s.price}" type="number" groupingUsed="true"/>
                </p>
            </div>
        </c:forEach>
    </div>
    <c:if test="${empty services}">
        <div class="empty-state">No services available right now.</div>
    </c:if>
</div>

<jsp:include page="/common/footer.jsp" />
