package com.example.legacy.servlet.filter;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CharacterEncodingFilter demonstrates:
 * - Character encoding configuration via filter
 * - Request/response wrapper usage
 * 
 * Migration Challenges:
 * 1. In Spring Boot, just set server.servlet.encoding.charset=UTF-8 in application.properties
 * 2. Spring Boot auto-configures CharacterEncodingFilter by default
 * 3. No need for manual filter registration
 */
public class CharacterEncodingFilter implements Filter {
    
    private static final Logger logger = Logger.getLogger(CharacterEncodingFilter.class);
    
    private String encoding = "UTF-8";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String encodingParam = filterConfig.getInitParameter("encoding");
        if (encodingParam != null) {
            this.encoding = encodingParam;
        }
        logger.info("CharacterEncodingFilter initialized with encoding: " + encoding);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Set request encoding
        if (httpRequest.getCharacterEncoding() == null) {
            httpRequest.setCharacterEncoding(encoding);
            logger.debug("Set request encoding to: " + encoding);
        }
        
        // Set response encoding
        httpResponse.setCharacterEncoding(encoding);
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("CharacterEncodingFilter destroyed");
    }
}
