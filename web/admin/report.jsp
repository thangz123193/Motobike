<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <div class="page-header">
        <div class="page-title">Financial report</div>
    </div>

    <div class="card" style="max-width:500px;">
        <form method="get" action="${pageContext.request.contextPath}/admin/report">
            <div class="form-row">
                <div><label for="from">From</label><input type="date" id="from" name="from" value="${from}"></div>
                <div><label for="to">To</label><input type="date" id="to" name="to" value="${to}"></div>
            </div>
            <button type="submit" class="btn btn-primary">Filter report</button>
        </form>
    </div>

    <div class="grid-4 mt-16">
        <div class="stat-box success">
            <div class="stat-value">$<fmt:formatNumber value="${revenue}" groupingUsed="true"/></div>
            <div class="stat-label">Revenue (completed orders)</div>
        </div>
        <div class="stat-box">
            <div class="stat-value">${completedCount}</div>
            <div class="stat-label">Orders completed</div>
        </div>
        <div class="stat-box warn">
            <div class="stat-value">${processingCount}</div>
            <div class="stat-label">Orders in progress</div>
        </div>
        <div class="stat-box danger">
            <div class="stat-value">${rejectedCount}</div>
            <div class="stat-label">Orders declined</div>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
