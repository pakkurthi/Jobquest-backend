package com.example.jobportal.service;

import com.example.jobportal.dto.CreateJobRequest;
import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.dto.UpdateJobRequest;
import com.example.jobportal.model.Job;
import com.example.jobportal.model.Role;
import com.example.jobportal.model.User;
import com.example.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobService {
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private AuthService authService;
    
    public JobResponse createJob(CreateJobRequest request) {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_PROVIDER) {
            throw new RuntimeException("Only Job Providers can create job postings");
        }
        
        Job job = new Job(request.getTitle(), request.getDescription(), 
                         request.getCompany(), request.getLocation(), currentUser);
        job.setSalary(request.getSalary());
        job.setJobType(request.getJobType());
        job.setExperienceLevel(request.getExperienceLevel());
        
        Job savedJob = jobRepository.save(job);
        return new JobResponse(savedJob);
    }
    
    public JobResponse updateJob(Long jobId, UpdateJobRequest request) {
        User currentUser = authService.getCurrentUser();
        
        Optional<Job> jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            throw new RuntimeException("Job not found");
        }
        
        Job job = jobOpt.get();
        
        // Check if user is the owner of the job
        if (!job.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own job postings");
        }
        
        // Update job fields
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setJobType(request.getJobType());
        job.setExperienceLevel(request.getExperienceLevel());
        
        if (request.getIsActive() != null) {
            job.setIsActive(request.getIsActive());
        }
        
        Job updatedJob = jobRepository.save(job);
        return new JobResponse(updatedJob);
    }
    
    public void deleteJob(Long jobId) {
        User currentUser = authService.getCurrentUser();
        
        Optional<Job> jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            throw new RuntimeException("Job not found");
        }
        
        Job job = jobOpt.get();
        
        // Check if user is the owner of the job
        if (!job.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own job postings");
        }
        
        // Soft delete by setting isActive to false
        job.setIsActive(false);
        jobRepository.save(job);
    }
    
    public List<JobResponse> getMyJobs() {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getRole() != Role.JOB_PROVIDER) {
            throw new RuntimeException("Only Job Providers can view their job postings");
        }
        
        List<Job> jobs = jobRepository.findByCreatedByOrderByCreatedAtDesc(currentUser);
        return jobs.stream()
                   .map(JobResponse::new)
                   .collect(Collectors.toList());
    }
    
    public List<JobResponse> getActiveJobs() {
        List<Job> jobs = jobRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        return jobs.stream()
                   .map(JobResponse::new)
                   .collect(Collectors.toList());
    }
    
    public JobResponse getJobById(Long jobId) {
        Optional<Job> jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            throw new RuntimeException("Job not found");
        }
        
        Job job = jobOpt.get();
        if (!job.getIsActive()) {
            throw new RuntimeException("Job is not active");
        }
        
        return new JobResponse(job);
    }
    
    public List<JobResponse> searchJobs(String keyword) {
        List<Job> jobs = jobRepository.searchActiveJobs(keyword);
        return jobs.stream()
                   .map(JobResponse::new)
                   .collect(Collectors.toList());
    }
    
    public List<JobResponse> getJobsByLocation(String location) {
        List<Job> jobs = jobRepository.findByLocationIgnoreCaseContainingAndIsActiveTrueOrderByCreatedAtDesc(location);
        return jobs.stream()
                   .map(JobResponse::new)
                   .collect(Collectors.toList());
    }
    
    public List<JobResponse> getJobsByType(String jobType) {
        List<Job> jobs = jobRepository.findByJobTypeAndIsActiveTrueOrderByCreatedAtDesc(jobType);
        return jobs.stream()
                   .map(JobResponse::new)
                   .collect(Collectors.toList());
    }
    
    public List<JobResponse> getJobsByExperienceLevel(String experienceLevel) {
        List<Job> jobs = jobRepository.findByExperienceLevelAndIsActiveTrueOrderByCreatedAtDesc(experienceLevel);
        return jobs.stream()
                   .map(JobResponse::new)
                   .collect(Collectors.toList());
    }
    
    public Long getMyJobsCount() {
        User currentUser = authService.getCurrentUser();
        return jobRepository.countByCreatedByAndIsActiveTrue(currentUser);
    }
}
