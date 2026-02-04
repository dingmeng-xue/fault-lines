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
 * - Traditional HttpServlet extension (vs Spring @Controller)
 * - Session management using HttpSession
 * - Manual request parameter handling
 * - Hardcoded business logic
 * - Forward to JSP for view rendering
 * 
 * Migration Challenges:
 * 1. Convert to Spring @Controller with @RequestMapping
 * 2. Replace HttpSession with stateless JWT or Spring Session
 * 3. Use @RequestParam for parameter binding
 * 4. Externalize configuration
 * 5. Replace JSP with Thymeleaf or REST endpoints
 */
public class LoginServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LoginServlet.class);
    
    // Hardcoded configuration - Challenge: Should be in application.properties
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int SESSION_TIMEOUT = 1800; // 30 minutes
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        logger.info("GET /login - Displaying login page");
        
        // Forward to JSP (Challenge: JSP limitations in Spring Boot JAR)
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Manual parameter extraction (Challenge: Spring uses @RequestParam)
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        logger.info("POST /login - Login attempt for user: " + username);
        
        // Session management (Challenge: Moving to stateless architecture)
        HttpSession session = request.getSession(true);
        
        // Track login attempts in session
        Integer attempts = (Integer) session.getAttribute("loginAttempts");
        if (attempts == null) {
            attempts = 0;
        }
        
        // Hardcoded authentication logic (Challenge: Use Spring Security)
        if (authenticate(username, password)) {
            logger.info("Login successful for user: " + username);
            
            // Store user info in session (Challenge: Use JWT or Spring Security context)
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
    
    /**
     * Hardcoded authentication - Challenge: Replace with Spring Security
     */
    private boolean authenticate(String username, String password) {
        // Extremely simplified authentication
        // In real app, would check against database
        return username != null && password != null && 
               ("admin".equals(username) && "admin123".equals(password) ||
                "user".equals(username) && "user123".equals(password));
    }
    
    /**
     * Hardcoded role assignment - Challenge: Use Spring Security authorities
     */
    private String getUserRole(String username) {
        if ("admin".equals(username)) {
            return "admin";
        }
        return "user";
    }
}
