<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Legacy Servlet Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="login-box">
            <h1>Legacy Servlet Application</h1>
            <h2>Login</h2>
            
            <c:if test="${not empty error}">
                <div class="error-message">
                    ${error}
                    <c:if test="${not empty remainingAttempts}">
                        <p>Remaining attempts: ${remainingAttempts}</p>
                    </c:if>
                </div>
            </c:if>
            
            <c:if test="${param.error eq 'true'}">
                <div class="error-message">
                    Login failed. Please check your credentials.
                </div>
            </c:if>
            
            <form method="POST" action="${pageContext.request.contextPath}/login">
                <div class="form-group">
                    <label for="username">Username:</label>
                    <input type="text" id="username" name="username" required autofocus>
                    <small>Try: admin/admin123 or user/user123</small>
                </div>
                
                <div class="form-group">
                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password" required>
                </div>
                
                <button type="submit" class="btn-primary">Login</button>
            </form>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
