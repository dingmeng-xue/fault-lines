package com.example.legacy.servlet.filter;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * RequestLoggingFilter demonstrates:
 * - Traditional servlet filter for cross-cutting concerns
 * - Manual request/response logging
 * - Performance timing
 * 
 * Migration Challenges:
 * 1. Replace with Spring's CommonsRequestLoggingFilter or custom @Component filter
 * 2. Use Spring AOP for aspect-oriented logging
 * 3. Use Spring Boot Actuator for request metrics
 * 4. Integrate with Spring Boot's logging framework
 */
public class RequestLoggingFilter implements Filter {
    
    private static final Logger logger = Logger.getLogger(RequestLoggingFilter.class);
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("RequestLoggingFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Log request details
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();
        String remoteAddr = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        
        String fullURL = uri + (queryString != null ? "?" + queryString : "");
        
        logger.info(String.format("Incoming request: %s %s from %s", method, fullURL, remoteAddr));
        logger.debug("User-Agent: " + userAgent);
        
        // Track request timing
        long startTime = System.currentTimeMillis();
        
        try {
            // Continue filter chain
            chain.doFilter(request, response);
            
        } finally {
            // Log response details and timing
            long duration = System.currentTimeMillis() - startTime;
            int status = httpResponse.getStatus();
            
            logger.info(String.format("Completed: %s %s - Status: %d - Duration: %dms", 
                                    method, fullURL, status, duration));
            
            // Warn on slow requests (Challenge: Spring Boot Actuator provides better metrics)
            if (duration > 1000) {
                logger.warn(String.format("Slow request detected: %s %s took %dms", 
                                        method, fullURL, duration));
            }
        }
    }
    
    @Override
    public void destroy() {
        logger.info("RequestLoggingFilter destroyed");
    }
}
