package com.example.jobportal.service;

import com.example.jobportal.dto.JobApplicationRequest;
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
public class JobApplicationService {
    
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private AuthService authService;
    
    /**
     * Apply for a job
     */
    public JobApplicationResponse applyForJob(JobApplicationRequest request) {
        User currentUser = authService.getCurrentUser();
        
        // Validate that current user is a job seeker
        if (currentUser.getRole() != Role.JOB_SEEKER) {
            throw new RuntimeException("Only Job Seekers can apply for jobs");
        }
        
        // Find the job
        Optional<Job> jobOpt = jobRepository.findById(request.getJobId());
        if (jobOpt.isEmpty()) {
            throw new RuntimeException("Job not found");
        }
        
        Job job = jobOpt.get();
        
        // Check if job is active
        if (!job.getIsActive()) {
            throw new RuntimeException("This job is no longer active");
        }
        
        // Check if user already applied
        Optional<JobApplication> existingApplication = jobApplicationRepository.findByJobAndApplicant(job, currentUser);
        if (existingApplication.isPresent()) {
            throw new RuntimeException("You have already applied for this job");
        }
        
        // Check if user is trying to apply to their own job posting
        if (job.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You cannot apply to your own job posting");
        }
        
        // Create new application
        JobApplication application = new JobApplication(job, currentUser, request.getCoverLetter());
        application.setResumeUrl(request.getResumeUrl());
        
        JobApplication savedApplication = jobApplicationRepository.save(application);
        return new JobApplicationResponse(savedApplication);
    }
    
    /**
     * Get all applications for current job seeker
     */
    public List<JobApplicationResponse> getMyApplications() {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_SEEKER) {
            throw new RuntimeException("Only Job Seekers can view their applications");
        }
        
        List<JobApplication> applications = jobApplicationRepository.findByApplicantOrderByAppliedAtDesc(currentUser);
        return applications.stream()
                .map(JobApplicationResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get applications by status for current job seeker
     */
    public List<JobApplicationResponse> getMyApplicationsByStatus(ApplicationStatus status) {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_SEEKER) {
            throw new RuntimeException("Only Job Seekers can view their applications");
        }
        
        List<JobApplication> applications = jobApplicationRepository
                .findByApplicantAndStatusOrderByAppliedAtDesc(currentUser, status);
        return applications.stream()
                .map(JobApplicationResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Withdraw application
     */
    public JobApplicationResponse withdrawApplication(Long applicationId) {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_SEEKER) {
            throw new RuntimeException("Only Job Seekers can withdraw applications");
        }
        
        Optional<JobApplication> applicationOpt = jobApplicationRepository.findById(applicationId);
        if (applicationOpt.isEmpty()) {
            throw new RuntimeException("Application not found");
        }
        
        JobApplication application = applicationOpt.get();
        
        // Check if current user owns this application
        if (!application.getApplicant().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only withdraw your own applications");
        }
        
        // Check if application can be withdrawn
        if (application.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new RuntimeException("Application is already withdrawn");
        }
        
        if (application.getStatus() == ApplicationStatus.ACCEPTED || 
            application.getStatus() == ApplicationStatus.REJECTED) {
            throw new RuntimeException("Cannot withdraw application with status: " + application.getStatus());
        }
        
        application.setStatus(ApplicationStatus.WITHDRAWN);
        JobApplication updatedApplication = jobApplicationRepository.save(application);
        
        return new JobApplicationResponse(updatedApplication);
    }
    
    /**
     * Get application by ID (for job seeker)
     */
    public JobApplicationResponse getApplicationById(Long applicationId) {
        User currentUser = authService.getCurrentUser();
        
        Optional<JobApplication> applicationOpt = jobApplicationRepository.findById(applicationId);
        if (applicationOpt.isEmpty()) {
            throw new RuntimeException("Application not found");
        }
        
        JobApplication application = applicationOpt.get();
        
        // Check permissions - either the applicant or the job provider can view
        boolean isApplicant = application.getApplicant().getId().equals(currentUser.getId());
        boolean isJobProvider = application.getJob().getCreatedBy().getId().equals(currentUser.getId());
        
        if (!isApplicant && !isJobProvider) {
            throw new RuntimeException("You don't have permission to view this application");
        }
        
        return new JobApplicationResponse(application);
    }
    
    /**
     * Get count of applications for current job seeker
     */
    public Long getMyApplicationsCount() {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_SEEKER) {
            throw new RuntimeException("Only Job Seekers can view their application count");
        }
        
        return jobApplicationRepository.countByApplicant(currentUser);
    }
    
    /**
     * Get count of applications by status for current job seeker
     */
    public Long getMyApplicationsCountByStatus(ApplicationStatus status) {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_SEEKER) {
            throw new RuntimeException("Only Job Seekers can view their application count");
        }
        
        return jobApplicationRepository.countByApplicantAndStatus(currentUser, status);
    }
    
    /**
     * Check if current user has applied for a specific job
     */
    public boolean hasAppliedForJob(Long jobId) {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_SEEKER) {
            return false;
        }
        
        Optional<Job> jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            return false;
        }
        
        Optional<JobApplication> application = jobApplicationRepository.findByJobAndApplicant(jobOpt.get(), currentUser);
        return application.isPresent();
    }
    
    /**
     * Get recent applications for current job seeker
     */
    public List<JobApplicationResponse> getRecentApplications() {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_SEEKER) {
            throw new RuntimeException("Only Job Seekers can view their applications");
        }
        
        List<JobApplication> applications = jobApplicationRepository.findTop10ByApplicantOrderByAppliedAtDesc(currentUser);
        return applications.stream()
                .map(JobApplicationResponse::new)
                .collect(Collectors.toList());
    }
}
