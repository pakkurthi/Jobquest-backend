package com.example.jobportal.controller;

import com.example.jobportal.dto.JobApplicationResponse;
import com.example.jobportal.dto.UpdateApplicationStatusRequest;
import com.example.jobportal.model.ApplicationStatus;
import com.example.jobportal.service.JobProviderApplicationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/provider")
@CrossOrigin(origins = "*", maxAge = 3600)
public class JobProviderController {
    
    @Autowired
    private JobProviderApplicationService jobProviderApplicationService;
    /**
     * Test endpoint to verify controller and authentication without service dependency
     */
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        System.out.println("=== JOB PROVIDER CONTROLLER TEST ENDPOINT ===");
        System.out.println("Test endpoint called successfully!");
        
        // Debug authentication
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Current Auth: " + (auth != null ? auth.getName() : "NULL"));
            System.out.println("Authenticated: " + (auth != null ? auth.isAuthenticated() : "NULL"));
            System.out.println("Authorities: " + (auth != null ? auth.getAuthorities() : "NULL"));
            
            if (auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_JOB_PROVIDER"))) {
                System.out.println("User has ROLE_JOB_PROVIDER authority!");
            } else {
                System.out.println("User DOES NOT have ROLE_JOB_PROVIDER authority!");
            }
        } catch (Exception e) {
            System.out.println("Error getting auth: " + e.getMessage());
        }
        
        System.out.println("=== END JOB PROVIDER CONTROLLER TEST ===");
        return ResponseEntity.ok("JobProviderController is working!");
    }
    
    /**
     * Get all applications for jobs posted by the current provider
     */
    @GetMapping("/applications")
    @PreAuthorize("hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<List<JobApplicationResponse>> getAllApplicationsForMyJobs() {
        try {
            List<JobApplicationResponse> applications = jobProviderApplicationService.getAllApplicationsForMyJobs();
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /**
     * Get applications for a specific job
     */
    @GetMapping("/jobs/{jobId}/applications")
    @PreAuthorize("hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<List<JobApplicationResponse>> getApplicationsForJob(@PathVariable Long jobId) {
        try {
            List<JobApplicationResponse> applications = jobProviderApplicationService.getApplicationsForJob(jobId);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /**
     * Update application status (review, accept, reject)
     */
    @PutMapping("/applications/{applicationId}/status")
    @PreAuthorize("hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<JobApplicationResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody UpdateApplicationStatusRequest request) {
        try {
            JobApplicationResponse updatedApplication = jobProviderApplicationService.updateApplicationStatus(
                applicationId, request.getStatus());
            return ResponseEntity.ok(updatedApplication);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /**
     * Get applications filtered by status
     */
    @GetMapping("/applications/status/{status}")
    @PreAuthorize("hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<List<JobApplicationResponse>> getApplicationsByStatus(@PathVariable ApplicationStatus status) {
        try {
            List<JobApplicationResponse> applications = jobProviderApplicationService.getApplicationsByStatus(status);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
