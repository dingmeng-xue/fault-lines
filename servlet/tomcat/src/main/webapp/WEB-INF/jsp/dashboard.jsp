<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Legacy Servlet Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>User Dashboard</h1>
            <div class="user-info">
                Welcome, <strong>${sessionScope.username}</strong> 
                (Role: ${sessionScope.role})
                <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
            </div>
        </header>
        
        <div class="dashboard-content">
            <div class="stats-box">
                <h2>Statistics</h2>
                <p>Total Users: <strong>${userCount}</strong></p>
                <p>Your Session ID: <strong>${pageContext.session.id}</strong></p>
                <p>Session Created: <strong>${sessionScope.loginTime}</strong></p>
            </div>
            
            <div class="menu-box">
                <h2>Quick Links</h2>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/users/list">View All Users (JSON)</a></li>
                    <li><a href="${pageContext.request.contextPath}/users/profile/${sessionScope.username}">My Profile</a></li>
                    <li><a href="${pageContext.request.contextPath}/products/list">View Products</a></li>
                    <li><a href="${pageContext.request.contextPath}/export?type=csv">Export Data (CSV)</a></li>
                    <c:if test="${sessionScope.role eq 'admin'}">
                        <li><a href="${pageContext.request.contextPath}/admin">Admin Panel</a></li>
                    </c:if>
                </ul>
            </div>
            
            <div class="challenge-box">
                <h2>Session Management Challenge</h2>
                <p>This page demonstrates traditional session-based state management:</p>
                <ul>
                    <li>User info stored in HttpSession</li>
                    <li>Session timeout: <%= session.getMaxInactiveInterval() %> seconds</li>
                    <li>Stateful architecture</li>
                </ul>
            </div>
        </div>
    </div>
</body>
</html>
