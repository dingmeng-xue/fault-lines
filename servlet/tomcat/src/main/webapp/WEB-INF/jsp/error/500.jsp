<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error - Internal Server Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="error-page">
            <h1>500</h1>
            <h2>Internal Server Error</h2>
            <p>Something went wrong on our end.</p>
            
            <% if (exception != null) { %>
                <div class="error-details">
                    <h3>Error Details:</h3>
                    <p><strong>Type:</strong> <%= exception.getClass().getName() %></p>
                    <p><strong>Message:</strong> <%= exception.getMessage() %></p>
                </div>
            <% } %>
            
            <a href="${pageContext.request.contextPath}/" class="btn-primary">Go Home</a>
        </div>
    </div>
</body>
</html>
