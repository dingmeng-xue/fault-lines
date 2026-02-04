<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="error-page">
            <h1>Error</h1>
            <h2>An unexpected error occurred</h2>
            
            <% if (exception != null) { %>
                <div class="error-details">
                    <p><strong>Exception:</strong> <%= exception.getClass().getName() %></p>
                    <p><strong>Message:</strong> <%= exception.getMessage() %></p>
                </div>
            <% } %>
            
            <a href="${pageContext.request.contextPath}/" class="btn-primary">Go Home</a>
        </div>
    </div>
</body>
</html>
