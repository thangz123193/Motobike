<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>500 - Server error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-box" style="text-align:center;">
        <h2>500 — Something went wrong</h2>
        <p class="subtitle">An unexpected error occurred. Please try again.</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/">Back to home</a>
    </div>
</div>
</body>
</html>
