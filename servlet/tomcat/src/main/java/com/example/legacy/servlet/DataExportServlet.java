package com.example.legacy.servlet;

import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DataExportServlet demonstrates:
 * - JNDI DataSource lookup (Tomcat container-managed)
 * - Direct JDBC usage (no ORM)
 * - Manual resource management
 * - CSV export functionality
 * 
 * Migration Challenges:
 * 1. Replace JNDI lookup with Spring DataSource bean injection
 * 2. Use Spring Data JPA instead of direct JDBC
 * 3. Use @Autowired for dependency injection
 * 4. Proper exception handling with @ExceptionHandler
 * 5. Use try-with-resources more consistently
 */
public class DataExportServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(DataExportServlet.class);
    
    // JNDI name from web.xml and context.xml (Challenge: Replace with Spring config)
    private static final String JNDI_NAME = "java:comp/env/jdbc/LegacyDB";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String exportType = request.getParameter("type");
        
        if (exportType == null) {
            exportType = "csv";
        }
        
        logger.info("Exporting data in format: " + exportType);
        
        try {
            if ("csv".equals(exportType)) {
                exportToCSV(response);
            } else if ("sql".equals(exportType)) {
                exportToSQL(response);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                                 "Unsupported export type. Use 'csv' or 'sql'");
            }
        } catch (Exception e) {
            logger.error("Error during data export", e);
            throw new ServletException("Export failed", e);
        }
    }
    
    /**
     * Demonstrates JNDI DataSource lookup
     * Challenge: In Spring Boot, just @Autowired DataSource dataSource
     */
    private DataSource getDataSource() throws NamingException {
        Context initContext = new InitialContext();
        return (DataSource) initContext.lookup(JNDI_NAME);
    }
    
    private void exportToCSV(HttpServletResponse response) 
            throws NamingException, SQLException, IOException {
        
        DataSource ds = getDataSource();
        
        // Manual connection management (Challenge: Spring manages this)
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ds.getConnection();
            
            // Create sample table if not exists (for demonstration)
            initializeSampleData(conn);
            
            stmt = conn.prepareStatement("SELECT * FROM USERS ORDER BY ID");
            rs = stmt.executeQuery();
            
            // Set response headers for CSV download
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"users.csv\"");
            
            PrintWriter writer = response.getWriter();
            
            // Write CSV header
            writer.println("ID,USERNAME,EMAIL,CREATED_DATE");
            
            // Write data rows
            while (rs.next()) {
                writer.printf("%d,%s,%s,%s%n",
                    rs.getLong("ID"),
                    rs.getString("USERNAME"),
                    rs.getString("EMAIL"),
                    rs.getTimestamp("CREATED_DATE")
                );
            }
            
            writer.flush();
            
            logger.info("CSV export completed successfully");
            
        } finally {
            // Manual resource cleanup (Challenge: Spring and try-with-resources handle this)
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { 
                    logger.error("Error closing ResultSet", e); 
                }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { 
                    logger.error("Error closing Statement", e); 
                }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { 
                    logger.error("Error closing Connection", e); 
                }
            }
        }
    }
    
    private void exportToSQL(HttpServletResponse response) 
            throws NamingException, SQLException, IOException {
        
        DataSource ds = getDataSource();
        
        try (Connection conn = ds.getConnection()) {
            
            initializeSampleData(conn);
            
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM USERS ORDER BY ID");
                 ResultSet rs = stmt.executeQuery()) {
            
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment; filename=\"users.sql\"");
            
            PrintWriter writer = response.getWriter();
            writer.println("-- User Data Export");
            writer.println("-- Generated: " + new java.util.Date());
            writer.println();
            
                while (rs.next()) {
                    writer.printf("INSERT INTO USERS (ID, USERNAME, EMAIL, CREATED_DATE) VALUES (%d, '%s', '%s', '%s');%n",
                        rs.getLong("ID"),
                        rs.getString("USERNAME"),
                        rs.getString("EMAIL"),
                        rs.getTimestamp("CREATED_DATE")
                    );
                }
                
                writer.flush();
                
                logger.info("SQL export completed successfully");
            }
        }
    }
    
    /**
     * Initialize sample data in H2 database
     * Challenge: In Spring Boot, use schema.sql and data.sql or Flyway/Liquibase
     */
    private void initializeSampleData(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS USERS (" +
                "ID BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "USERNAME VARCHAR(50) NOT NULL, " +
                "EMAIL VARCHAR(100) NOT NULL, " +
                "CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")) {
            stmt.execute();
        }
        
        // Check if data exists
        try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM USERS");
             ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert sample data
                try (PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO USERS (USERNAME, EMAIL) VALUES (?, ?)")) {
                    
                    String[][] users = {
                        {"admin", "admin@example.com"},
                        {"john", "john@example.com"},
                        {"jane", "jane@example.com"},
                        {"bob", "bob@example.com"}
                    };
                    
                    for (String[] user : users) {
                        insertStmt.setString(1, user[0]);
                        insertStmt.setString(2, user[1]);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }
}
