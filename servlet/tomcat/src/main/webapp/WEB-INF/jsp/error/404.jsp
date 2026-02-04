<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error - Page Not Found</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="error-page">
            <h1>404</h1>
            <h2>Page Not Found</h2>
            <p>The page you are looking for does not exist.</p>
            <a href="${pageContext.request.contextPath}/" class="btn-primary">Go Home</a>
            
            <div class="error-details">
                <p><strong>Challenge:</strong> Error pages configured in web.xml</p>
                <p><strong>Migration:</strong> Use Spring Boot's ErrorController or @ControllerAdvice</p>
            </div>
        </div>
    </div>
</body>
</html>
