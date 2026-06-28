<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Manage parts</div>
    </div>
    <c:if test="${param.success == '1'}"><div class="alert alert-success">Done.</div></c:if>

    <div class="grid-2">
        <div class="card">
            <h3>Add a new part</h3>
            <form method="post" action="${pageContext.request.contextPath}/admin/parts">
                <input type="hidden" name="action" value="create">
                <label for="partName">Part name</label>
                <input type="text" id="partName" name="partName" required>
                <div class="form-row">
                    <div><label for="unit">Unit</label><input type="text" id="unit" name="unit" placeholder="piece, set, can..."></div>
                    <div><label for="price">Price ($)</label><input type="number" id="price" name="price" min="0" required></div>
                    <div><label for="quantity">Initial stock</label><input type="number" id="quantity" name="quantity" min="0" value="0"></div>
                </div>
                <label for="description">Description</label>
                <textarea id="description" name="description" rows="2"></textarea>
                <button type="submit" class="btn btn-primary">Add part</button>
            </form>
        </div>

        <div class="card">
            <h3>Parts list</h3>
            <table>
                <thead><tr><th>Name</th><th>Price</th><th>Stock</th><th>Status</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="p" items="${parts}">
                    <tr>
                        <td>${p.partName}</td>
                        <td>$<fmt:formatNumber value="${p.price}" groupingUsed="true"/></td>
                        <td>${p.quantity}</td>
                        <td>
                            <c:if test="${p.active}"><span class="badge badge-completed">Active</span></c:if>
                            <c:if test="${!p.active}"><span class="badge badge-rejected">Hidden</span></c:if>
                        </td>
                        <td>
                            <button class="btn btn-sm btn-secondary" onclick="document.getElementById('edit${p.partID}').style.display='block'">Edit</button>
                            <c:if test="${p.active}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/parts" style="display:inline;">
                                    <input type="hidden" name="action" value="deactivate">
                                    <input type="hidden" name="partId" value="${p.partID}">
                                    <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Hide this part?')">Hide</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                    <tr id="edit${p.partID}" style="display:none;">
                        <td colspan="5">
                            <form method="post" action="${pageContext.request.contextPath}/admin/parts">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="partId" value="${p.partID}">
                                <div class="form-row">
                                    <div><label>Name</label><input type="text" name="partName" value="${p.partName}" required></div>
                                    <div><label>Unit</label><input type="text" name="unit" value="${p.unit}"></div>
                                    <div><label>Price</label><input type="number" name="price" value="${p.price}" min="0" required></div>
                                    <div><label>Stock</label><input type="number" name="quantity" value="${p.quantity}" min="0"></div>
                                    <div><label>Status</label>
                                        <select name="isActive">
                                            <option value="true" ${p.active ? 'selected' : ''}>Active</option>
                                            <option value="false" ${!p.active ? 'selected' : ''}>Hidden</option>
                                        </select>
                                    </div>
                                </div>
                                <label>Description</label>
                                <textarea name="description" rows="2">${p.description}</textarea>
                                <button type="submit" class="btn btn-sm btn-primary">Save</button>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/admin/parts" style="margin-top:8px;">
                                <input type="hidden" name="action" value="restock">
                                <input type="hidden" name="partId" value="${p.partID}">
                                <div class="form-row">
                                    <div><label>Restock amount</label><input type="number" name="amount" min="1" value="10"></div>
                                    <div style="flex:0 0 auto; align-self:flex-end;"><button type="submit" class="btn btn-sm btn-success">+ Restock</button></div>
                                </div>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <c:if test="${empty parts}"><div class="empty-state">No parts yet.</div></c:if>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
