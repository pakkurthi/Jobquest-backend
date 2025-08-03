package com.example.jobportal.repository;

import com.example.jobportal.model.Job;
import com.example.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    List<Job> findByIsActiveTrueOrderByCreatedAtDesc();
    
    List<Job> findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(User createdBy);
    
    List<Job> findByCreatedByOrderByCreatedAtDesc(User createdBy);
    
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Job> searchActiveJobs(@Param("keyword") String keyword);
    
    List<Job> findByLocationIgnoreCaseContainingAndIsActiveTrueOrderByCreatedAtDesc(String location);
    
    List<Job> findByJobTypeAndIsActiveTrueOrderByCreatedAtDesc(String jobType);
    
    List<Job> findByExperienceLevelAndIsActiveTrueOrderByCreatedAtDesc(String experienceLevel);
    
    Long countByCreatedByAndIsActiveTrue(User createdBy);
}
