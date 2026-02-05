package com.example.legacy.servlet.listener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;
import java.util.Properties;

/**
 * ApplicationStartupListener demonstrates:
 * - ServletContextListener for application lifecycle events
 * - Application initialization logic
 * - JNDI environment entry lookup
 * - Log4j configuration
 * - Startup/shutdown hooks
 */
public class ApplicationStartupListener implements ServletContextListener {
    
    private static final Logger logger = Logger.getLogger(ApplicationStartupListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        logger.info("========================================");
        logger.info("Application Starting Up...");
        logger.info("========================================");
        
        // Configure Log4j
        configureLogging(context);
        
        // Read context parameters
        String environment = context.getInitParameter("appEnvironment");
        String maxUploadSize = context.getInitParameter("maxUploadSize");
        String apiEndpoint = context.getInitParameter("apiEndpoint");
        
        logger.info("Environment: " + environment);
        logger.info("Max Upload Size: " + maxUploadSize + " bytes");
        logger.info("API Endpoint: " + apiEndpoint);
        
        // Store in application scope
        context.setAttribute("startTime", System.currentTimeMillis());
        context.setAttribute("environment", environment);
        
        // Lookup JNDI environment entries
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            
            String appName = (String) envContext.lookup("appName");
            Integer maxCacheSize = (Integer) envContext.lookup("maxCacheSize");
            
            logger.info("JNDI App Name: " + appName);
            logger.info("JNDI Max Cache Size: " + maxCacheSize);
            
            context.setAttribute("appName", appName);
            context.setAttribute("maxCacheSize", maxCacheSize);
            
        } catch (Exception e) {
            logger.warn("Failed to lookup JNDI resources (may not be available in all environments)", e);
        }
        
        // Initialize application components
        initializeCache(context);
        initializeScheduledTasks();
        loadApplicationConfig();
        
        logger.info("Application started successfully");
        logger.info("Context Path: " + context.getContextPath());
        logger.info("Server Info: " + context.getServerInfo());
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        logger.info("========================================");
        logger.info("Application Shutting Down...");
        logger.info("========================================");
        
        // Calculate uptime
        Long startTime = (Long) context.getAttribute("startTime");
        if (startTime != null) {
            long uptime = System.currentTimeMillis() - startTime;
            long hours = uptime / (1000 * 60 * 60);
            long minutes = (uptime % (1000 * 60 * 60)) / (1000 * 60);
            long seconds = (uptime % (1000 * 60)) / 1000;
            
            logger.info(String.format("Application uptime: %d hours, %d minutes, %d seconds", 
                                    hours, minutes, seconds));
        }
        
        // Cleanup resources
        cleanupCache();
        shutdownScheduledTasks();
        closeConnections();
        
        logger.info("Application shutdown complete");
    }
    
    /**
     * Configure Log4j from properties file
     */
    private void configureLogging(ServletContext context) {
        try {
            InputStream log4jConfig = context.getResourceAsStream("/WEB-INF/log4j.properties");
            if (log4jConfig != null) {
                Properties props = new Properties();
                props.load(log4jConfig);
                PropertyConfigurator.configure(props);
                logger.info("Log4j configured from /WEB-INF/log4j.properties");
            }
        } catch (Exception e) {
            System.err.println("Failed to configure Log4j: " + e.getMessage());
        }
    }
    
    /**
     * Initialize application cache
     */
    private void initializeCache(ServletContext context) {
        Integer maxCacheSize = (Integer) context.getAttribute("maxCacheSize");
        if (maxCacheSize == null) {
            maxCacheSize = 100;
        }
        
        logger.info("Initializing cache with max size: " + maxCacheSize);
        // Cache initialization would go here
    }
    
    /**
     * Initialize scheduled tasks
     */
    private void initializeScheduledTasks() {
        logger.info("Initializing scheduled tasks");
        // Would typically use Timer or ScheduledExecutorService
    }
    
    /**
     * Load application configuration
     */
    private void loadApplicationConfig() {
        logger.info("Loading application configuration");
        // Load config from properties files, database, etc.
    }
    
    /**
     * Cleanup methods
     */
    private void cleanupCache() {
        logger.info("Cleaning up cache");
    }
    
    private void shutdownScheduledTasks() {
        logger.info("Shutting down scheduled tasks");
    }
    
    private void closeConnections() {
        logger.info("Closing database connections and other resources");
    }
}
