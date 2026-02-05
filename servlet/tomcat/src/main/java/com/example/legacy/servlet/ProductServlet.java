package com.example.legacy.servlet;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductServlet demonstrates:
 * - Traditional servlet pattern
 * - Hardcoded data/configuration
 * - Manual JSON handling
 * 
 * Migration Challenges:
 * 1. Convert to Spring @RestController
 * 2. Use Spring Data repositories
 * 3. Automatic JSON conversion with Jackson
 * 4. Pagination support with Spring Data
 */
public class ProductServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(ProductServlet.class);
    
    // Hardcoded product catalog (Challenge: Use database with Spring Data JPA)
    private static List<Product> products = new ArrayList<>();
    
    static {
        products.add(new Product(1L, "Laptop", "High-performance laptop", new BigDecimal("999.99"), 10));
        products.add(new Product(2L, "Mouse", "Wireless mouse", new BigDecimal("29.99"), 50));
        products.add(new Product(3L, "Keyboard", "Mechanical keyboard", new BigDecimal("79.99"), 30));
        products.add(new Product(4L, "Monitor", "27-inch 4K monitor", new BigDecimal("399.99"), 15));
        products.add(new Product(5L, "Headphones", "Noise-canceling headphones", new BigDecimal("199.99"), 25));
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
            listProducts(request, response);
        } else if (pathInfo.startsWith("/details/")) {
            String idStr = pathInfo.substring("/details/".length());
            try {
                Long id = Long.parseLong(idStr);
                getProductDetails(request, response, id);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void listProducts(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Manual pagination (Challenge: Spring Data provides this automatically)
        String pageParam = request.getParameter("page");
        String sizeParam = request.getParameter("size");
        
        int page = 0;
        int size = 10;
        
        try {
            if (pageParam != null) {
                page = Math.max(0, Integer.parseInt(pageParam));
            }
            if (sizeParam != null) {
                size = Math.max(1, Math.min(100, Integer.parseInt(sizeParam)));
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid pagination parameters");
            return;
        }
        
        int start = Math.min(page * size, products.size());
        int end = Math.min(start + size, products.size());
        
        List<Product> pageProducts = products.subList(start, end);
        
        // Manual JSON building (Challenge: Spring Boot does this automatically)
        JSONObject result = new JSONObject();
        result.put("totalElements", products.size());
        result.put("totalPages", (products.size() + size - 1) / size);
        result.put("currentPage", page);
        result.put("pageSize", size);
        
        JSONArray productsArray = new JSONArray();
        for (Product product : pageProducts) {
            productsArray.put(productToJson(product));
        }
        result.put("products", productsArray);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(result.toString());
    }
    
    private void getProductDetails(HttpServletRequest request, HttpServletResponse response, Long id) 
            throws IOException, ServletException {
        
        Product product = findProductById(id);
        
        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
            return;
        }
        
        // Check if JSON response is requested
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(productToJson(product).toString());
        } else {
            // Forward to JSP for HTML view
            request.setAttribute("product", product);
            request.getRequestDispatcher("/WEB-INF/jsp/product-details.jsp").forward(request, response);
        }
    }
    
    private Product findProductById(Long id) {
        for (Product p : products) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
    
    private JSONObject productToJson(Product product) {
        JSONObject json = new JSONObject();
        json.put("id", product.getId());
        json.put("name", product.getName());
        json.put("description", product.getDescription());
        json.put("price", product.getPrice());
        json.put("stock", product.getStock());
        return json;
    }
    
    // Simple Product class (Challenge: Should be JPA entity)
    private static class Product {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private int stock;
        
        public Product(Long id, String name, String description, BigDecimal price, int stock) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.stock = stock;
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public BigDecimal getPrice() { return price; }
        public int getStock() { return stock; }
    }
}
