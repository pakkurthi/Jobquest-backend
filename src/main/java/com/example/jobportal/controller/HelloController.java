package com.example.jobportal.controller;

import com.example.jobportal.model.HealthCheck;
import com.example.jobportal.repository.HealthCheckRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final HealthCheckRepository healthCheckRepository;

    public HelloController(HealthCheckRepository healthCheckRepository) {
        this.healthCheckRepository = healthCheckRepository;
    }

    @GetMapping("/api/hello")
    public String hello() {
        long count = healthCheckRepository.count();
        return "rrDB is connected! Total health records - AUTO RESTART WORKS : " + count;
    }
    
    @GetMapping("/api/debug")
    public ResponseEntity<String> debugEndpoint() {
        System.out.println("=== DEBUG ENDPOINT ===");
        
        // Debug authentication
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Current Auth: " + (auth != null ? auth.getName() : "NULL"));
            System.out.println("Authenticated: " + (auth != null ? auth.isAuthenticated() : "NULL"));
            System.out.println("Authorities: " + (auth != null ? auth.getAuthorities() : "NULL"));
            
            return ResponseEntity.ok("Debug info: Auth = " + auth);
        } catch (Exception e) {
            System.out.println("Error getting auth: " + e.getMessage());
            return ResponseEntity.ok("Debug error: " + e.getMessage());
        }
    }
}
