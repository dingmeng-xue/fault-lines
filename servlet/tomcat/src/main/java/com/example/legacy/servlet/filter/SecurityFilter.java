package com.example.legacy.servlet.filter;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * SecurityFilter demonstrates:
 * - Traditional Filter-based security
 * - Session-based authentication checking
 * - Manual URL pattern matching
 * - Filter init parameters from web.xml
 */
public class SecurityFilter implements Filter {
    
    private static final Logger logger = Logger.getLogger(SecurityFilter.class);
    
    private Set<String> excludePatterns = new HashSet<>();
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String excludeParam = filterConfig.getInitParameter("excludePatterns");
        
        if (excludeParam != null) {
            String[] patterns = excludeParam.split(",");
            for (String pattern : patterns) {
                excludePatterns.add(pattern.trim());
            }
        }
        
        logger.info("SecurityFilter initialized with exclude patterns: " + excludePatterns);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        logger.debug("SecurityFilter checking: " + path);
        
        // Check if path should be excluded from security check
        if (isExcluded(path)) {
            logger.debug("Path excluded from security check: " + path);
            chain.doFilter(request, response);
            return;
        }
        
        HttpSession session = httpRequest.getSession(false);
        
        if (session == null || session.getAttribute("username") == null) {
            logger.warn("Unauthorized access attempt to: " + path);
            
            // Save original URL for redirect after login
            httpRequest.getSession(true).setAttribute("originalURL", requestURI);
            
            // Redirect to login page
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }
        
        // Check role-based access for admin paths
        if (path.startsWith("/admin")) {
            String role = (String) session.getAttribute("role");
            
            if (!"admin".equals(role)) {
                logger.warn("Forbidden access attempt by user: " + 
                          session.getAttribute("username") + " to: " + path);
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
                                     "You don't have permission to access this resource");
                return;
            }
        }
        
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        
        logger.debug("Access granted to: " + path);
        
        // Continue filter chain
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("SecurityFilter destroyed");
    }
    
    private boolean isExcluded(String path) {
        for (String pattern : excludePatterns) {
            if (pattern.endsWith("/*")) {
                String prefix = pattern.substring(0, pattern.length() - 2);
                if (path.startsWith(prefix)) {
                    return true;
                }
            } else if (pattern.equals(path)) {
                return true;
            }
        }
        return false;
    }
}
