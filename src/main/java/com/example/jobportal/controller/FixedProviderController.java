package com.example.jobportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/provider-fixed")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FixedProviderController {
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("FixedProviderController test endpoint called");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Auth: " + (auth != null ? auth.getName() : "NULL"));
        return ResponseEntity.ok("FixedProviderController is working!");
    }
    
    @GetMapping("/test-auth")
    public ResponseEntity<String> testAuth() {
        System.out.println("FixedProviderController testAuth endpoint called");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authInfo = "Authentication: " + 
                          "\nName: " + (auth != null ? auth.getName() : "NULL") +
                          "\nAuthenticated: " + (auth != null ? auth.isAuthenticated() : "NULL") +
                          "\nAuthorities: " + (auth != null ? auth.getAuthorities() : "NULL");
        return ResponseEntity.ok(authInfo);
    }
    
    @GetMapping("/secured")
    @PreAuthorize("hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<String> secured() {
        System.out.println("FixedProviderController secured endpoint called");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok("This is a secured endpoint! Authenticated as: " + auth.getName());
    }
}
