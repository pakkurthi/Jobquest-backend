package com.example.jobportal.controller;

import com.example.jobportal.dto.CreateJobRequest;
import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.dto.UpdateJobRequest;
import com.example.jobportal.service.JobService;
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
@RequestMapping("/api/jobs")
public class JobController {
    
    @Autowired
    private JobService jobService;
    
    @GetMapping("/test-provider")
    @PreAuthorize("hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<String> testProviderEndpoint() {
        return ResponseEntity.ok("JobController test for provider is working!");
    }
    
    // Job Provider APIs
    
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody CreateJobRequest request) {
        try {
            JobResponse jobResponse = jobService.createJob(request);
            return ResponseEntity.ok(jobResponse);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @PutMapping("/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long jobId, 
                                               @Valid @RequestBody UpdateJobRequest request) {
        try {
            JobResponse jobResponse = jobService.updateJob(jobId, request);
            return ResponseEntity.ok(jobResponse);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<Map<String, String>> deleteJob(@PathVariable Long jobId) {
        try {
            jobService.deleteJob(jobId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Job deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @GetMapping("/my-jobs")
    @PreAuthorize("hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<List<JobResponse>> getMyJobs() {
        try {
            List<JobResponse> jobs = jobService.getMyJobs();
            return ResponseEntity.ok(jobs);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @GetMapping("/my-jobs/count")
    @PreAuthorize("hasAuthority('ROLE_JOB_PROVIDER')")
    public ResponseEntity<Map<String, Long>> getMyJobsCount() {
        try {
            Long count = jobService.getMyJobsCount();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    // Public APIs (accessible to all users and job seekers)
    
    @GetMapping("/public/all")
    public ResponseEntity<List<JobResponse>> getAllActiveJobs() {
        List<JobResponse> jobs = jobService.getActiveJobs();
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/public/{jobId}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long jobId) {
        try {
            JobResponse job = jobService.getJobById(jobId);
            return ResponseEntity.ok(job);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @GetMapping("/public/search")
    public ResponseEntity<List<JobResponse>> searchJobs(@RequestParam String keyword) {
        List<JobResponse> jobs = jobService.searchJobs(keyword);
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/public/location")
    public ResponseEntity<List<JobResponse>> getJobsByLocation(@RequestParam String location) {
        List<JobResponse> jobs = jobService.getJobsByLocation(location);
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/public/type")
    public ResponseEntity<List<JobResponse>> getJobsByType(@RequestParam String jobType) {
        List<JobResponse> jobs = jobService.getJobsByType(jobType);
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/public/experience")
    public ResponseEntity<List<JobResponse>> getJobsByExperienceLevel(@RequestParam String experienceLevel) {
        List<JobResponse> jobs = jobService.getJobsByExperienceLevel(experienceLevel);
        return ResponseEntity.ok(jobs);
    }
}