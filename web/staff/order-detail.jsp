<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Repair order #${order.orderID}</div>
    </div>

    <c:if test="${param.success == '1'}"><div class="alert alert-success">Updated successfully.</div></c:if>
    <c:if test="${param.error == '1'}"><div class="alert alert-error">Something went wrong. Please try again.</div></c:if>
    <c:if test="${param.error == 'stock'}"><div class="alert alert-error">Not enough stock for this part.</div></c:if>

    <div class="grid-2">
        <div class="card">
            <h3>Order details</h3>
            <p><strong>Customer:</strong> ${order.customerName} (${order.customerPhone})</p>
            <p><strong>License plate:</strong> ${order.licensePlate}</p>
            <p><strong>Issue:</strong> ${order.description}</p>
            <p><strong>Status:</strong>
                <c:choose>
                    <c:when test="${order.status == 'PENDING'}"><span class="badge badge-pending">Pending</span></c:when>
                    <c:when test="${order.status == 'ACCEPTED'}"><span class="badge badge-accepted">Accepted</span></c:when>
                    <c:when test="${order.status == 'PROCESSING'}"><span class="badge badge-processing">In progress</span></c:when>
                    <c:when test="${order.status == 'COMPLETED'}"><span class="badge badge-completed">Completed</span></c:when>
                    <c:when test="${order.status == 'REJECTED'}"><span class="badge badge-rejected">Declined</span></c:when>
                </c:choose>
            </p>
            <p><strong>Created:</strong> <fmt:formatDate value="${order.createdDate}" pattern="MMM d, yyyy h:mm a"/></p>

            <hr class="divider">
            <h4>Actions</h4>
            <div style="display:flex; gap:8px; flex-wrap:wrap; margin-top:8px;">
                <c:if test="${order.status == 'PENDING'}">
                    <form method="post" action="${pageContext.request.contextPath}/staff/order-action" style="display:inline;">
                        <input type="hidden" name="orderId" value="${order.orderID}">
                        <input type="hidden" name="action" value="accept">
                        <button type="submit" class="btn btn-success">Accept order</button>
                    </form>
                    <button type="button" class="btn btn-danger" onclick="document.getElementById('rejectBox').style.display='block'">Decline</button>
                </c:if>
                <c:if test="${order.status == 'ACCEPTED'}">
                    <form method="post" action="${pageContext.request.contextPath}/staff/order-action" style="display:inline;">
                        <input type="hidden" name="orderId" value="${order.orderID}">
                        <input type="hidden" name="action" value="start">
                        <button type="submit" class="btn btn-primary">Start repair</button>
                    </form>
                </c:if>
                <c:if test="${order.status == 'PROCESSING'}">
                    <form method="post" action="${pageContext.request.contextPath}/staff/order-action" style="display:inline;"
                          onsubmit="return confirm('Mark this order as completed?');">
                        <input type="hidden" name="orderId" value="${order.orderID}">
                        <input type="hidden" name="action" value="complete">
                        <button type="submit" class="btn btn-success">Mark completed</button>
                    </form>
                </c:if>
            </div>

            <c:if test="${order.status == 'PENDING'}">
                <div id="rejectBox" style="display:none; margin-top:14px;">
                    <form method="post" action="${pageContext.request.contextPath}/staff/order-action">
                        <input type="hidden" name="orderId" value="${order.orderID}">
                        <input type="hidden" name="action" value="reject">
                        <label for="reason">Reason for declining</label>
                        <textarea id="reason" name="reason" rows="2" required></textarea>
                        <button type="submit" class="btn btn-danger">Confirm decline</button>
                    </form>
                </div>
            </c:if>
        </div>

        <div class="card">
            <h3>Cost breakdown (parts + services)</h3>
            <table>
                <thead><tr><th>Item</th><th>Qty</th><th>Unit price</th><th>Total</th></tr></thead>
                <tbody>
                <c:forEach var="d" items="${details}">
                    <tr>
                        <td>${d.itemName} <span class="text-muted">(${d.itemType == 'PART' ? 'Part' : 'Service'})</span></td>
                        <td>${d.quantity}</td>
                        <td>$<fmt:formatNumber value="${d.unitPrice}" groupingUsed="true"/></td>
                        <td>$<fmt:formatNumber value="${d.lineTotal}" groupingUsed="true"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <c:if test="${empty details}"><p class="text-muted">No items added yet.</p></c:if>

            <c:if test="${order.status == 'ACCEPTED' || order.status == 'PROCESSING'}">
                <hr class="divider">
                <form method="post" action="${pageContext.request.contextPath}/staff/add-item">
                    <input type="hidden" name="orderId" value="${order.orderID}">
                    <div class="form-row">
                        <div>
                            <label for="itemType">Type</label>
                            <select id="itemType" name="itemType" onchange="toggleItemSelect()">
                                <option value="SERVICE">Service</option>
                                <option value="PART">Part</option>
                            </select>
                        </div>
                        <div>
                            <label for="quantity">Quantity</label>
                            <input type="number" id="quantity" name="quantity" value="1" min="1">
                        </div>
                    </div>
                    <div id="serviceSelectBox">
                        <label for="serviceId">Select service</label>
                        <select id="serviceId" name="itemId">
                            <c:forEach var="s" items="${services}">
                                <option value="${s.serviceID}">${s.serviceName} - $<fmt:formatNumber value="${s.price}" groupingUsed="true"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div id="partSelectBox" style="display:none;">
                        <label for="partId">Select part</label>
                        <select id="partId">
                            <c:forEach var="p" items="${parts}">
                                <option value="${p.partID}">${p.partName} (${p.quantity} in stock) - $<fmt:formatNumber value="${p.price}" groupingUsed="true"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary">Add to order</button>
                </form>
                <script>
                    function toggleItemSelect() {
                        var type = document.getElementById('itemType').value;
                        var serviceSel = document.getElementById('serviceSelectBox');
                        var partSel = document.getElementById('partSelectBox');
                        var serviceInput = serviceSel.querySelector('select');
                        var partInput = partSel.querySelector('select');
                        if (type === 'SERVICE') {
                            serviceSel.style.display = 'block';
                            partSel.style.display = 'none';
                            serviceInput.name = 'itemId';
                            partInput.removeAttribute('name');
                        } else {
                            serviceSel.style.display = 'none';
                            partSel.style.display = 'block';
                            partInput.name = 'itemId';
                            serviceInput.removeAttribute('name');
                        }
                    }
                </script>
            </c:if>

            <hr class="divider">
            <p class="text-right" style="font-weight:700; font-size:16px;">
                Total: $<fmt:formatNumber value="${order.totalCost}" groupingUsed="true"/>
            </p>
        </div>
    </div>

    <div class="card">
        <h3>Inspection &amp; repair log</h3>
        <c:forEach var="log" items="${logs}">
            <p style="margin-bottom:10px;">
                <strong>${log.staffName}</strong>
                <span class="text-muted">(<fmt:formatDate value="${log.createdDate}" pattern="MMM d, yyyy h:mm a"/>)</span><br>
                ${log.note}
            </p>
        </c:forEach>
        <c:if test="${empty logs}"><p class="text-muted">No notes added yet.</p></c:if>

        <c:if test="${order.status == 'ACCEPTED' || order.status == 'PROCESSING'}">
            <hr class="divider">
            <form method="post" action="${pageContext.request.contextPath}/staff/add-log">
                <input type="hidden" name="orderId" value="${order.orderID}">
                <label for="note">Add an inspection note</label>
                <textarea id="note" name="note" rows="3" required placeholder="e.g. Checked the brake system, found worn brake pads..."></textarea>
                <button type="submit" class="btn btn-secondary">Add note</button>
            </form>
        </c:if>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
