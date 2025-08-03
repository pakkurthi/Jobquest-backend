package com.example.jobportal.controller;

import com.example.jobportal.dto.AuthResponse;
import com.example.jobportal.dto.LoginRequest;
import com.example.jobportal.dto.SignupRequest;
import com.example.jobportal.model.User;
import com.example.jobportal.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password!");
        }
    }
    
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            AuthResponse authResponse = authService.registerUser(signUpRequest);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_JOB_SEEKER') or hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        User currentUser = authService.getCurrentUser();
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", currentUser.getId());
        response.put("email", currentUser.getEmail());
        response.put("firstName", currentUser.getFirstName());
        response.put("lastName", currentUser.getLastName());
        response.put("role", currentUser.getRole());
        response.put("phoneNumber", currentUser.getPhoneNumber());
        response.put("bio", currentUser.getBio());
        response.put("createdAt", currentUser.getCreatedAt());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test/jobseeker")
    @PreAuthorize("hasAuthority('ROLE_JOB_SEEKER')")
    public ResponseEntity<String> jobSeekerAccess() {
        return ResponseEntity.ok("Job Seeker Content.");
    }
    
    @GetMapping("/test/jobprovider")
    @PreAuthorize("hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<String> jobProviderAccess() {
        return ResponseEntity.ok("Job Provider Content.");
    }
}
