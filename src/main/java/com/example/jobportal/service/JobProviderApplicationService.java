package com.example.jobportal.service;

import com.example.jobportal.dto.JobApplicationResponse;
import com.example.jobportal.model.*;
import com.example.jobportal.repository.JobApplicationRepository;
import com.example.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobProviderApplicationService {
    
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private AuthService authService;
    
    /**
     * Get all applications for jobs created by current job provider
     */
    public List<JobApplicationResponse> getAllApplicationsForMyJobs() {
        System.out.println("=== JOB PROVIDER APPLICATION SERVICE DEBUG ===");
        System.out.println("JobProviderApplicationService.getAllApplicationsForMyJobs() called");
        
        try {
            System.out.println("Getting current user...");
            User currentUser = authService.getCurrentUser();
            System.out.println("Current user retrieved: " + currentUser.getEmail() + ", Role: " + currentUser.getRole());
            
            if (currentUser.getRole() != Role.JOB_PROVIDER) {
                System.out.println("ERROR: User is not a JOB_PROVIDER. Role: " + currentUser.getRole());
                throw new RuntimeException("Only Job Providers can view applications for their jobs");
            }
            
            System.out.println("Querying database for applications...");
            List<JobApplication> applications = jobApplicationRepository.findByJobProviderOrderByAppliedAtDesc(currentUser);
            System.out.println("Found " + applications.size() + " applications");
            
            List<JobApplicationResponse> responses = applications.stream()
                    .map(JobApplicationResponse::new)
                    .collect(Collectors.toList());
            
            System.out.println("Converted to " + responses.size() + " response objects");
            System.out.println("=== END JOB PROVIDER APPLICATION SERVICE DEBUG ===");
            
            return responses;
        } catch (Exception e) {
            System.out.println("ERROR in JobProviderApplicationService: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Get applications for a specific job (must be owned by current user)
     */
    public List<JobApplicationResponse> getApplicationsForJob(Long jobId) {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_PROVIDER) {
            throw new RuntimeException("Only Job Providers can view applications for their jobs");
        }
        
        Optional<Job> jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            throw new RuntimeException("Job not found");
        }
        
        Job job = jobOpt.get();
        
        // Check if current user owns this job
        if (!job.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only view applications for your own job postings");
        }
        
        List<JobApplication> applications = jobApplicationRepository.findByJobOrderByAppliedAtDesc(job);
        return applications.stream()
                .map(JobApplicationResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get applications by status for current job provider
     */
    public List<JobApplicationResponse> getApplicationsByStatus(ApplicationStatus status) {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_PROVIDER) {
            throw new RuntimeException("Only Job Providers can view applications for their jobs");
        }
        
        List<JobApplication> applications = jobApplicationRepository
                .findByJobProviderAndStatusOrderByAppliedAtDesc(currentUser, status);
        return applications.stream()
                .map(JobApplicationResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Update application status (only job provider can do this for their jobs)
     */
    public JobApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatus newStatus) {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_PROVIDER) {
            throw new RuntimeException("Only Job Providers can update application status");
        }
        
        Optional<JobApplication> applicationOpt = jobApplicationRepository.findById(applicationId);
        if (applicationOpt.isEmpty()) {
            throw new RuntimeException("Application not found");
        }
        
        JobApplication application = applicationOpt.get();
        
        // Check if current user owns the job for this application
        if (!application.getJob().getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update applications for your own job postings");
        }
        
        // Validate status transition
        if (application.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new RuntimeException("Cannot update status of withdrawn application");
        }
        
        if (newStatus == ApplicationStatus.WITHDRAWN) {
            throw new RuntimeException("Only job seekers can withdraw applications");
        }
        
        application.setStatus(newStatus);
        JobApplication updatedApplication = jobApplicationRepository.save(application);
        
        return new JobApplicationResponse(updatedApplication);
    }
    
    /**
     * Get total count of applications for current job provider
     */
    public Long getTotalApplicationsCount() {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_PROVIDER) {
            throw new RuntimeException("Only Job Providers can view application statistics");
        }
        
        List<JobApplication> applications = jobApplicationRepository.findByJobProviderOrderByAppliedAtDesc(currentUser);
        return (long) applications.size();
    }
    
    /**
     * Get count of applications by status for current job provider
     */
    public Long getApplicationsCountByStatus(ApplicationStatus status) {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_PROVIDER) {
            throw new RuntimeException("Only Job Providers can view application statistics");
        }
        
        return jobApplicationRepository.countByJobProviderAndStatus(currentUser, status);
    }
    
    /**
     * Get count of applications for a specific job
     */
    public Long getApplicationsCountForJob(Long jobId) {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_PROVIDER) {
            throw new RuntimeException("Only Job Providers can view application statistics");
        }
        
        Optional<Job> jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            throw new RuntimeException("Job not found");
        }
        
        Job job = jobOpt.get();
        
        // Check if current user owns this job
        if (!job.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only view statistics for your own job postings");
        }
        
        return jobApplicationRepository.countByJob(job);
    }
    
    /**
     * Get recent applications for current job provider
     */
    public List<JobApplicationResponse> getRecentApplications() {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_PROVIDER) {
            throw new RuntimeException("Only Job Providers can view applications");
        }
        
        List<JobApplication> applications = jobApplicationRepository.findTop10ByJobProviderOrderByAppliedAtDesc(currentUser);
        return applications.stream()
                .map(JobApplicationResponse::new)
                .collect(Collectors.toList());
    }
}
