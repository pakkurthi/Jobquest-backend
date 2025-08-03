package com.example.jobportal.repository;

import com.example.jobportal.model.ApplicationStatus;
import com.example.jobportal.model.Job;
import com.example.jobportal.model.JobApplication;
import com.example.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    
    // Find all applications by applicant (job seeker)
    List<JobApplication> findByApplicantOrderByAppliedAtDesc(User applicant);
    
    // Find all applications by job (for job providers to see who applied)
    List<JobApplication> findByJobOrderByAppliedAtDesc(Job job);
    
    // Find applications by job provider (all applications for jobs created by a specific user)
    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.createdBy = :jobProvider ORDER BY ja.appliedAt DESC")
    List<JobApplication> findByJobProviderOrderByAppliedAtDesc(@Param("jobProvider") User jobProvider);
    
    // Check if user already applied to a job
    Optional<JobApplication> findByJobAndApplicant(Job job, User applicant);
    
    // Find applications by status for a job seeker
    List<JobApplication> findByApplicantAndStatusOrderByAppliedAtDesc(User applicant, ApplicationStatus status);
    
    // Find applications by status for a job provider
    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.createdBy = :jobProvider AND ja.status = :status ORDER BY ja.appliedAt DESC")
    List<JobApplication> findByJobProviderAndStatusOrderByAppliedAtDesc(@Param("jobProvider") User jobProvider, @Param("status") ApplicationStatus status);
    
    // Count applications for a job seeker
    long countByApplicant(User applicant);
    
    // Count applications for a specific job
    long countByJob(Job job);
    
    // Count applications by status for a job seeker
    long countByApplicantAndStatus(User applicant, ApplicationStatus status);
    
    // Count applications by status for a job provider
    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.job.createdBy = :jobProvider AND ja.status = :status")
    long countByJobProviderAndStatus(@Param("jobProvider") User jobProvider, @Param("status") ApplicationStatus status);
    
    // Get recent applications for a job seeker
    List<JobApplication> findTop10ByApplicantOrderByAppliedAtDesc(User applicant);
    
    // Get recent applications for a job provider
    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.createdBy = :jobProvider ORDER BY ja.appliedAt DESC LIMIT 10")
    List<JobApplication> findTop10ByJobProviderOrderByAppliedAtDesc(@Param("jobProvider") User jobProvider);
}
