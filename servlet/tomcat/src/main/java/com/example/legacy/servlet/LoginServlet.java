package com.example.legacy.servlet;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * LoginServlet demonstrates:
 * - Traditional HttpServlet extension
 * - Session management using HttpSession
 * - Manual request parameter handling
 * - Hardcoded business logic
 * - Forward to JSP for view rendering
 */
public class LoginServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LoginServlet.class);
    
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int SESSION_TIMEOUT = 1800; // 30 minutes
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        logger.info("GET /login - Displaying login page");
        
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        logger.info("POST /login - Login attempt for user: " + username);
        
        HttpSession session = request.getSession(true);
        
        // Track login attempts in session
        Integer attempts = (Integer) session.getAttribute("loginAttempts");
        if (attempts == null) {
            attempts = 0;
        }
        
        if (authenticate(username, password)) {
            logger.info("Login successful for user: " + username);
            
            session.setAttribute("username", username);
            session.setAttribute("role", getUserRole(username));
            session.setAttribute("loginTime", System.currentTimeMillis());
            session.removeAttribute("loginAttempts");
            
            // Set custom session timeout
            session.setMaxInactiveInterval(SESSION_TIMEOUT);
            
            // Redirect to home page
            response.sendRedirect(request.getContextPath() + "/users/dashboard");
            
        } else {
            logger.warn("Login failed for user: " + username);
            
            attempts++;
            session.setAttribute("loginAttempts", attempts);
            
            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                logger.error("Maximum login attempts exceeded for user: " + username);
                session.invalidate();
                request.setAttribute("error", "Too many failed attempts. Please try again later.");
            } else {
                request.setAttribute("error", "Invalid username or password");
                request.setAttribute("remainingAttempts", MAX_LOGIN_ATTEMPTS - attempts);
            }
            
            // Forward back to login page with error
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }
    
    private boolean authenticate(String username, String password) {
        // Extremely simplified authentication
        // In real app, would check against database
        return username != null && password != null && 
               ("admin".equals(username) && "admin123".equals(password) ||
                "user".equals(username) && "user123".equals(password));
    }
    
    private String getUserRole(String username) {
        if ("admin".equals(username)) {
            return "admin";
        }
        return "user";
    }
}
