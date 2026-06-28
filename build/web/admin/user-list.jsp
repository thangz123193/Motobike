<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Manage accounts</div>
    </div>

    <c:if test="${param.success == '1'}"><div class="alert alert-success">Done.</div></c:if>
    <c:if test="${param.error == 'self_lockout'}"><div class="alert alert-error">You can't lock your own account.</div></c:if>
    <c:if test="${param.error == 'self_downgrade'}"><div class="alert alert-error">You can't downgrade your own role.</div></c:if>
    <c:if test="${param.error == 'self_delete'}"><div class="alert alert-error">You can't delete your own account.</div></c:if>
    <c:if test="${param.error == 'cannot_delete'}"><div class="alert alert-error">This account can't be deleted because it's referenced by other orders/bookings.</div></c:if>
    <c:if test="${param.error == 'duplicate'}"><div class="alert alert-error">This username already exists.</div></c:if>
    <c:if test="${param.error == 'invalid'}"><div class="alert alert-error">Invalid information.</div></c:if>

    <div class="card">
        <h3>Create a new staff account</h3>
        <form method="post" action="${pageContext.request.contextPath}/admin/users">
            <input type="hidden" name="action" value="createStaff">
            <div class="form-row">
                <div><label>Username</label><input type="text" name="username" required></div>
                <div><label>Password</label><input type="password" name="password" minlength="6" required></div>
                <div><label>Full name</label><input type="text" name="fullName" required></div>
                <div><label>Phone</label><input type="text" name="phone"></div>
                <div><label>Email</label><input type="email" name="email"></div>
            </div>
            <button type="submit" class="btn btn-primary">Create staff account</button>
        </form>
    </div>

    <div class="card">
        <form method="get" action="${pageContext.request.contextPath}/admin/users" style="display:flex; gap:10px; margin-bottom:16px;">
            <select name="role" style="max-width:180px; margin-bottom:0;">
                <option value="0" ${roleFilter == 0 ? 'selected' : ''}>All roles</option>
                <option value="1" ${roleFilter == 1 ? 'selected' : ''}>Admin</option>
                <option value="2" ${roleFilter == 2 ? 'selected' : ''}>Staff</option>
                <option value="3" ${roleFilter == 3 ? 'selected' : ''}>Customer</option>
            </select>
            <input type="text" name="keyword" placeholder="Search by name, username, phone..." value="${keyword}" style="margin-bottom:0;">
            <button type="submit" class="btn btn-secondary" style="flex:0 0 auto;">Search</button>
        </form>

        <table>
            <thead><tr><th>Full name</th><th>Username</th><th>Phone</th><th>Role</th><th>Status</th><th></th></tr></thead>
            <tbody>
            <c:forEach var="u" items="${users}">
                <tr>
                    <td>${u.fullName}</td>
                    <td>${u.username}</td>
                    <td>${u.phone}</td>
                    <td>${u.roleName}</td>
                    <td>
                        <c:if test="${u.active}"><span class="badge badge-completed">Active</span></c:if>
                        <c:if test="${!u.active}"><span class="badge badge-rejected">Locked</span></c:if>
                    </td>
                    <td>
                        <button class="btn btn-sm btn-secondary" onclick="document.getElementById('manage${u.userID}').style.display='block'">Manage</button>
                    </td>
                </tr>
                <tr id="manage${u.userID}" style="display:none;">
                    <td colspan="6">
                        <div style="display:flex; gap:10px; flex-wrap:wrap; align-items:flex-end;">
                            <form method="post" action="${pageContext.request.contextPath}/admin/users">
                                <input type="hidden" name="action" value="toggleActive">
                                <input type="hidden" name="userId" value="${u.userID}">
                                <input type="hidden" name="active" value="${u.active ? '0' : '1'}">
                                <button type="submit" class="btn btn-sm ${u.active ? 'btn-danger' : 'btn-success'}">
                                    ${u.active ? 'Lock account' : 'Unlock account'}
                                </button>
                            </form>

                            <form method="post" action="${pageContext.request.contextPath}/admin/users" style="display:flex; gap:6px; align-items:flex-end;">
                                <input type="hidden" name="action" value="changeRole">
                                <input type="hidden" name="userId" value="${u.userID}">
                                <select name="roleId" style="margin-bottom:0;">
                                    <option value="1" ${u.roleID == 1 ? 'selected' : ''}>Admin</option>
                                    <option value="2" ${u.roleID == 2 ? 'selected' : ''}>Staff</option>
                                    <option value="3" ${u.roleID == 3 ? 'selected' : ''}>Customer</option>
                                </select>
                                <button type="submit" class="btn btn-sm btn-secondary">Change role</button>
                            </form>

                            <form method="post" action="${pageContext.request.contextPath}/admin/users" style="display:flex; gap:6px; align-items:flex-end;">
                                <input type="hidden" name="action" value="resetPassword">
                                <input type="hidden" name="userId" value="${u.userID}">
                                <input type="password" name="newPassword" placeholder="New password" minlength="6" required style="margin-bottom:0;">
                                <button type="submit" class="btn btn-sm btn-secondary">Reset password</button>
                            </form>

                            <form method="post" action="${pageContext.request.contextPath}/admin/users"
                                  onsubmit="return confirm('Delete this account?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="userId" value="${u.userID}">
                                <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <c:if test="${empty users}"><div class="empty-state">No accounts found.</div></c:if>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
