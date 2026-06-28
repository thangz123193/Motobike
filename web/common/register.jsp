<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign up - MotorRepair</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-box" style="width:440px;">
        <h2>Create a customer account</h2>
        <p class="subtitle">Sign up to book repairs online</p>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/register">
            <label for="fullName">Full name</label>
            <input type="text" id="fullName" name="fullName" value="${fullName}" required autofocus>

            <div class="form-row">
                <div>
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" value="${username}" required minlength="4">
                </div>
                <div>
                    <label for="phone">Phone</label>
                    <input type="text" id="phone" name="phone" value="${phone}" required pattern="\d{9,11}">
                </div>
            </div>

            <div class="form-row">
                <div>
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required minlength="6">
                </div>
                <div>
                    <label for="confirmPassword">Confirm password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required minlength="6">
                </div>
            </div>

            <label for="email">Email (optional)</label>
            <input type="email" id="email" name="email" value="${email}">

            <label for="address">Address (optional)</label>
            <input type="text" id="address" name="address" value="${address}">

            <button type="submit" class="btn btn-primary">Sign up</button>
        </form>
        <div class="auth-footer">
            Already have an account? <a href="${pageContext.request.contextPath}/login">Sign in</a>
        </div>
    </div>
</div>
</body>
</html>
