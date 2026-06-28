<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">New walk-in repair order</div>
    </div>

    <div class="card" style="max-width:640px;">
        <c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

        <form method="post" action="${pageContext.request.contextPath}/admin/create-order">
            <h4>Customer info</h4>
            <div class="form-row">
                <div>
                    <label for="fullName">Customer name *</label>
                    <input type="text" id="fullName" name="fullName" required>
                </div>
                <div>
                    <label for="phone">Phone number *</label>
                    <input type="text" id="phone" name="phone" required>
                </div>
            </div>
            <p class="text-muted" style="margin-top:-10px; margin-bottom:14px;">If this phone number already exists in the system, the order will be linked to that customer's account automatically.</p>

            <h4>Vehicle info</h4>
            <div class="form-row">
                <div>
                    <label for="licensePlate">License plate *</label>
                    <input type="text" id="licensePlate" name="licensePlate" required>
                </div>
                <div>
                    <label for="brand">Brand</label>
                    <input type="text" id="brand" name="brand">
                </div>
                <div>
                    <label for="model">Model</label>
                    <input type="text" id="model" name="model">
                </div>
            </div>

            <h4>Repair info</h4>
            <label for="description">Describe the issue</label>
            <textarea id="description" name="description" rows="3"></textarea>

            <div class="form-row">
                <div>
                    <label for="estimatedCost">Estimated cost</label>
                    <input type="number" id="estimatedCost" name="estimatedCost" value="0" min="0">
                </div>
                <div>
                    <label for="assignedStaffId">Assign technician (optional)</label>
                    <select id="assignedStaffId" name="assignedStaffId">
                        <option value="">— Not assigned —</option>
                        <c:forEach var="s" items="${staffList}">
                            <option value="${s.userID}">${s.fullName}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <button type="submit" class="btn btn-primary">Create repair order</button>
        </form>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
