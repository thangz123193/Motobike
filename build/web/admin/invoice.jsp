<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Invoice #${order.orderID}</title>
    <style>
        body { font-family: -apple-system, "Inter", "Segoe UI", Arial, sans-serif; padding: 48px; color: #16181d; max-width: 720px; margin: 0 auto; }
        .invoice-header { display:flex; justify-content:space-between; align-items:flex-start; border-bottom: 2px solid #161b26; padding-bottom: 20px; margin-bottom: 28px; }
        .invoice-header h1 { color: #161b26; font-size: 20px; margin: 0 0 4px; }
        .invoice-header p { margin: 0; color: #5b6270; font-size: 13px; }
        table { width: 100%; border-collapse: collapse; margin-top: 16px; font-size: 13.5px; }
        th, td { padding: 10px 12px; border-bottom: 1px solid #e8e8e8; text-align: left; }
        th { color: #5b6270; font-size: 11px; text-transform: uppercase; letter-spacing: 0.04em; }
        .text-right { text-align: right; }
        .total-row td { font-weight: 700; font-size: 15px; border-top: 2px solid #161b26; border-bottom: none; }
        .print-btn { margin-top: 28px; }
        .print-btn button { background: #e35c33; color: #fff; border: none; padding: 10px 20px; border-radius: 6px; font-size: 13.5px; font-weight: 600; cursor: pointer; }
        @media print { .print-btn { display: none; } }
    </style>
</head>
<body>
    <div class="invoice-header">
        <div>
            <h1>MotorRepair</h1>
            <p>Repair invoice</p>
        </div>
        <div class="text-right">
            <p><strong>Invoice #:</strong> ${order.orderID}</p>
            <p><strong>Date:</strong> <fmt:formatDate value="${order.completedDate}" pattern="MMM d, yyyy"/></p>
        </div>
    </div>

    <p><strong>Customer:</strong> ${order.customerName} (${order.customerPhone})</p>
    <p><strong>License plate:</strong> ${order.licensePlate}</p>
    <p><strong>Technician:</strong> ${order.assignedStaffName}</p>
    <p><strong>Description:</strong> ${order.description}</p>

    <table>
        <thead><tr><th>Item</th><th>Type</th><th>Qty</th><th class="text-right">Unit price</th><th class="text-right">Total</th></tr></thead>
        <tbody>
        <c:forEach var="d" items="${details}">
            <tr>
                <td>${d.itemName}</td>
                <td>${d.itemType == 'PART' ? 'Part' : 'Service'}</td>
                <td>${d.quantity}</td>
                <td class="text-right">$<fmt:formatNumber value="${d.unitPrice}" groupingUsed="true"/></td>
                <td class="text-right">$<fmt:formatNumber value="${d.lineTotal}" groupingUsed="true"/></td>
            </tr>
        </c:forEach>
        <tr class="total-row">
            <td colspan="4" class="text-right">TOTAL</td>
            <td class="text-right">$<fmt:formatNumber value="${order.totalCost}" groupingUsed="true"/></td>
        </tr>
        </tbody>
    </table>

    <div class="print-btn">
        <button onclick="window.print()">Print invoice</button>
    </div>
</body>
</html>
