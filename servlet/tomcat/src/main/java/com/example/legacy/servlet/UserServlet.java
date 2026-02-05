package com.example.legacy.servlet;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserServlet demonstrates:
 * - Manual routing based on pathInfo
 * - Session-based authorization checks
 * - Manual JSON serialization
 * - In-memory data storage
 * - Manual response building
 */
public class UserServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(UserServlet.class);
    
    private static Map<String, User> users = new HashMap<>();
    
    static {
        // Initialize with some data
        users.put("admin", new User("admin", "Administrator", "admin@example.com", "admin"));
        users.put("user", new User("user", "Regular User", "user@example.com", "user"));
        users.put("john", new User("john", "John Doe", "john@example.com", "user"));
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!isAuthenticated(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login first");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
            showDashboard(request, response);
        } else if (pathInfo.equals("/list")) {
            listUsers(request, response);
        } else if (pathInfo.startsWith("/profile/")) {
            String username = pathInfo.substring("/profile/".length());
            showUserProfile(request, response, username);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!isAuthenticated(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // Create new user
        String username = request.getParameter("username");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String role = request.getParameter("role");
        
        if (username == null || username.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username is required");
            return;
        }
        
        User user = new User(username, fullName, email, role);
        users.put(username, user);
        
        logger.info("Created new user: " + username);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        JSONObject json = new JSONObject();
        json.put("success", true);
        json.put("message", "User created successfully");
        json.put("user", userToJson(user));
        
        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!isAuthenticated(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // Note: Traditional servlets don't parse PUT parameters automatically
        // This is a simplified example
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"PUT not fully implemented in legacy servlet\"}");
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!isAuthenticated(request) || !isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/delete/")) {
            String username = pathInfo.substring("/delete/".length());
            users.remove(username);
            
            logger.info("Deleted user: " + username);
            
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": true, \"message\": \"User deleted\"}");
        }
    }
    
    private void showDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        request.setAttribute("username", session.getAttribute("username"));
        request.setAttribute("userCount", users.size());
        request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
    }
    
    private void listUsers(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        JSONArray jsonArray = new JSONArray();
        for (User user : users.values()) {
            jsonArray.put(userToJson(user));
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        out.print(jsonArray.toString());
        out.flush();
    }
    
    private void showUserProfile(HttpServletRequest request, HttpServletResponse response, 
                                 String username) throws ServletException, IOException {
        User user = users.get(username);
        
        if (user == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }
        
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(request, response);
    }
    
    private boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("username") != null;
    }
    
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && "admin".equals(session.getAttribute("role"));
    }
    
    private JSONObject userToJson(User user) {
        JSONObject json = new JSONObject();
        json.put("username", user.getUsername());
        json.put("fullName", user.getFullName());
        json.put("email", user.getEmail());
        json.put("role", user.getRole());
        return json;
    }
    
    private static class User {
        private String username;
        private String fullName;
        private String email;
        private String role;
        
        public User(String username, String fullName, String email, String role) {
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
        }
        
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
    }
}
