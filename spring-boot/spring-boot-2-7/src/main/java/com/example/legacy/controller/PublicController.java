package com.example.legacy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Public endpoint that doesn't require authentication.
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("version", "2.7.18");
        response.put("springBoot", "2.7.x");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> response = new HashMap<>();
        response.put("application", "Legacy Spring Boot App");
        response.put("description", "Sample app with Spring Boot 2.7 features that break in 3.0");
        response.put("breaking-changes", "javax->jakarta, WebSecurityConfigurerAdapter, etc.");
        return ResponseEntity.ok(response);
    }

}
