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
            
            <!-- 
                JSP Challenge: 
                - JSPs have significant limitations in Spring Boot executable JARs
                - Must use WAR packaging or migrate to Thymeleaf/FreeMarker
                - JSP standard tags and JSTL work differently
            -->
            
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
                </div>
                
                <div class="form-group">
                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password" required>
                </div>
                
                <button type="submit" class="btn-primary">Login</button>
            </form>
            
            <div class="info-box">
                <h3>Migration Challenges Demonstrated:</h3>
                <ul>
                    <li><strong>JSP Limitations:</strong> This JSP won't work in Spring Boot executable JAR</li>
                    <li><strong>Session Management:</strong> Uses HttpSession (should be stateless in modern apps)</li>
                    <li><strong>Manual Security:</strong> Custom filter-based security (use Spring Security)</li>
                    <li><strong>Static Assets:</strong> CSS in /css/ needs to move to /static/ in Spring Boot</li>
                </ul>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
