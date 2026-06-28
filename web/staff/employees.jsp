<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Technician team</div>
    </div>
    <div class="card">
        <table>
            <thead><tr><th>Full name</th><th>Username</th><th>Phone</th><th>Email</th><th>Status</th></tr></thead>
            <tbody>
            <c:forEach var="s" items="${staffList}">
                <tr>
                    <td>${s.fullName}</td>
                    <td>${s.username}</td>
                    <td>${s.phone}</td>
                    <td>${s.email}</td>
                    <td>
                        <c:if test="${s.active}"><span class="badge badge-completed">Active</span></c:if>
                        <c:if test="${!s.active}"><span class="badge badge-rejected">Locked</span></c:if>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <c:if test="${empty staffList}"><div class="empty-state">No technicians found.</div></c:if>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
