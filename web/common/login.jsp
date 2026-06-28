<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign in - MotorRepair</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-box">
        <h2>MotorRepair</h2>
        <p class="subtitle">Sign in to continue</p>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        <c:if test="${param.error == 'locked'}">
            <div class="alert alert-error">Your account has been locked.</div>
        </c:if>
        <c:if test="${param.registered == '1'}">
            <div class="alert alert-success">Account created. Please sign in.</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <c:if test="${not empty param.redirect}">
                <input type="hidden" name="redirect" value="${param.redirect}">
            </c:if>
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required autofocus>

            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>

            <button type="submit" class="btn btn-primary">Sign in</button>
        </form>
        <div class="auth-footer">
            Don't have an account? <a href="${pageContext.request.contextPath}/register">Sign up</a>
        </div>
        <div class="auth-footer text-muted">
            Demo: admin/123456 &middot; staff01/123456 &middot; customer01/123456
        </div>
    </div>
</div>
</body>
</html>
