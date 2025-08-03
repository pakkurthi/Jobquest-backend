package com.example.jobportal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class JobApplicationRequest {
    
    @NotNull(message = "Job ID is required")
    private Long jobId;
    
    @Size(max = 1000, message = "Cover letter cannot exceed 1000 characters")
    private String coverLetter;
    
    @Size(max = 255, message = "Resume URL cannot exceed 255 characters")
    private String resumeUrl;
    
    // Constructors
    public JobApplicationRequest() {}
    
    public JobApplicationRequest(Long jobId, String coverLetter) {
        this.jobId = jobId;
        this.coverLetter = coverLetter;
    }
    
    // Getters and Setters
    public Long getJobId() {
        return jobId;
    }
    
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    
    public String getCoverLetter() {
        return coverLetter;
    }
    
    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }
    
    public String getResumeUrl() {
        return resumeUrl;
    }
    
    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
}
