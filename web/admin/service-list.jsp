<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Manage services</div>
    </div>
    <c:if test="${param.success == '1'}"><div class="alert alert-success">Done.</div></c:if>

    <div class="grid-2">
        <div class="card">
            <h3>Add a new service</h3>
            <form method="post" action="${pageContext.request.contextPath}/admin/services">
                <input type="hidden" name="action" value="create">
                <label for="serviceName">Service name</label>
                <input type="text" id="serviceName" name="serviceName" required>
                <label for="description">Description</label>
                <textarea id="description" name="description" rows="2"></textarea>
                <label for="price">Price ($)</label>
                <input type="number" id="price" name="price" min="0" required>
                <button type="submit" class="btn btn-primary">Add service</button>
            </form>
        </div>

        <div class="card">
            <h3>Service list</h3>
            <table>
                <thead><tr><th>Name</th><th>Price</th><th>Status</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="s" items="${services}">
                    <tr>
                        <td>${s.serviceName}</td>
                        <td>$<fmt:formatNumber value="${s.price}" groupingUsed="true"/></td>
                        <td>
                            <c:if test="${s.active}"><span class="badge badge-completed">Active</span></c:if>
                            <c:if test="${!s.active}"><span class="badge badge-rejected">Hidden</span></c:if>
                        </td>
                        <td>
                            <button class="btn btn-sm btn-secondary" onclick="document.getElementById('edit${s.serviceID}').style.display='block'">Edit</button>
                            <c:if test="${s.active}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/services" style="display:inline;">
                                    <input type="hidden" name="action" value="deactivate">
                                    <input type="hidden" name="serviceId" value="${s.serviceID}">
                                    <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Hide this service?')">Hide</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                    <tr id="edit${s.serviceID}" style="display:none;">
                        <td colspan="4">
                            <form method="post" action="${pageContext.request.contextPath}/admin/services">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="serviceId" value="${s.serviceID}">
                                <div class="form-row">
                                    <div><label>Name</label><input type="text" name="serviceName" value="${s.serviceName}" required></div>
                                    <div><label>Price</label><input type="number" name="price" value="${s.price}" min="0" required></div>
                                    <div><label>Status</label>
                                        <select name="isActive">
                                            <option value="true" ${s.active ? 'selected' : ''}>Active</option>
                                            <option value="false" ${!s.active ? 'selected' : ''}>Hidden</option>
                                        </select>
                                    </div>
                                </div>
                                <label>Description</label>
                                <textarea name="description" rows="2">${s.description}</textarea>
                                <button type="submit" class="btn btn-sm btn-primary">Save</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <c:if test="${empty services}"><div class="empty-state">No services yet.</div></c:if>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
