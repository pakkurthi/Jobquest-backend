package com.example.jobportal.controller;

import com.example.jobportal.dto.JobApplicationRequest;
import com.example.jobportal.dto.JobApplicationResponse;
import com.example.jobportal.model.ApplicationStatus;
import com.example.jobportal.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/job-seeker")
public class JobSeekerController {
    
    @Autowired
    private JobApplicationService jobApplicationService;
    
    // Job Application Management APIs
    
    @PostMapping("/apply")
    @PreAuthorize("hasAuthority('ROLE_JOB_SEEKER')")
    public ResponseEntity<JobApplicationResponse> applyForJob(@Valid @RequestBody JobApplicationRequest request) {
        try {
            JobApplicationResponse response = jobApplicationService.applyForJob(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @GetMapping("/applications")
    @PreAuthorize("hasAuthority('ROLE_JOB_SEEKER')")
    public ResponseEntity<List<JobApplicationResponse>> getMyApplications() {
        try {
            List<JobApplicationResponse> applications = jobApplicationService.getMyApplications();
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @GetMapping("/applications/status/{status}")
    @PreAuthorize("hasAuthority('ROLE_JOB_SEEKER')")
    public ResponseEntity<List<JobApplicationResponse>> getMyApplicationsByStatus(@PathVariable ApplicationStatus status) {
        try {
            List<JobApplicationResponse> applications = jobApplicationService.getMyApplicationsByStatus(status);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @GetMapping("/applications/{applicationId}")
    @PreAuthorize("hasAuthority('ROLE_JOB_SEEKER') or hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<JobApplicationResponse> getApplicationById(@PathVariable Long applicationId) {
        try {
            JobApplicationResponse application = jobApplicationService.getApplicationById(applicationId);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @PutMapping("/applications/{applicationId}/withdraw")
    @PreAuthorize("hasAuthority('ROLE_JOB_SEEKER')")
    public ResponseEntity<JobApplicationResponse> withdrawApplication(@PathVariable Long applicationId) {
        try {
            JobApplicationResponse response = jobApplicationService.withdrawApplication(applicationId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @GetMapping("/applications/count")
    @PreAuthorize("hasAuthority('ROLE_JOB_SEEKER')")
    public ResponseEntity<Map<String, Long>> getMyApplicationsCount() {
        try {
            Long count = jobApplicationService.getMyApplicationsCount();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @GetMapping("/applications/count/{status}")
    @PreAuthorize("hasAuthority('ROLE_JOB_SEEKER')")
    public ResponseEntity<Map<String, Object>> getMyApplicationsCountByStatus(@PathVariable ApplicationStatus status) {
        try {
            Long count = jobApplicationService.getMyApplicationsCountByStatus(status);
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            response.put("status", status.name()); // Include status for reference
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @GetMapping("/applications/recent")
    @PreAuthorize("hasAuthority('ROLE_JOB_SEEKER')")
    public ResponseEntity<List<JobApplicationResponse>> getRecentApplications() {
        try {
            List<JobApplicationResponse> applications = jobApplicationService.getRecentApplications();
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @GetMapping("/jobs/{jobId}/applied")
    @PreAuthorize("hasAuthority('ROLE_JOB_SEEKER')")
    public ResponseEntity<Map<String, Boolean>> hasAppliedForJob(@PathVariable Long jobId) {
        try {
            boolean hasApplied = jobApplicationService.hasAppliedForJob(jobId);
            Map<String, Boolean> response = new HashMap<>();
            response.put("hasApplied", hasApplied);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    // Dashboard/Statistics APIs
    
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasAuthority('ROLE_JOB_SEEKER')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Get total applications count
            Long totalApplications = jobApplicationService.getMyApplicationsCount();
            stats.put("totalApplications", totalApplications);
            
            // Get count by status
            Map<String, Long> statusCounts = new HashMap<>();
            for (ApplicationStatus status : ApplicationStatus.values()) {
                Long count = jobApplicationService.getMyApplicationsCountByStatus(status);
                statusCounts.put(status.name().toLowerCase(), count);
            }
            stats.put("applicationsByStatus", statusCounts);
            
            // Get recent applications (last 5)
            List<JobApplicationResponse> recentApplications = jobApplicationService.getRecentApplications();
            stats.put("recentApplications", recentApplications.subList(0, Math.min(5, recentApplications.size())));
            
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
