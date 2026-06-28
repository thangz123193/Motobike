<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Customers</div>
    </div>

    <div class="card">
        <form method="get" action="${pageContext.request.contextPath}/admin/customers" style="display:flex; gap:10px; margin-bottom:16px;">
            <input type="text" name="keyword" placeholder="Search by name, username, phone..." value="${keyword}" style="margin-bottom:0;">
            <button type="submit" class="btn btn-secondary" style="flex:0 0 auto;">Search</button>
        </form>

        <table>
            <thead><tr><th>Full name</th><th>Phone</th><th>Email</th><th>Address</th><th></th></tr></thead>
            <tbody>
            <c:forEach var="c" items="${customers}">
                <tr>
                    <td>${c.fullName}</td>
                    <td>${c.phone}</td>
                    <td>${c.email}</td>
                    <td>${c.address}</td>
                    <td><a href="${pageContext.request.contextPath}/admin/customers?id=${c.userID}">View details</a></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <c:if test="${empty customers}"><div class="empty-state">No customers found.</div></c:if>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
