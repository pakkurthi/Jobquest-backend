package com.example.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateJobRequest {
    
    @NotBlank(message = "Job title is required")
    @Size(max = 100)
    private String title;
    
    @NotBlank(message = "Job description is required")
    @Size(max = 2000)
    private String description;
    
    @Size(max = 100)
    private String company;
    
    @Size(max = 100)
    private String location;
    
    @Positive(message = "Salary must be positive")
    private BigDecimal salary;
    
    @Size(max = 50)
    private String jobType; // FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP
    
    @Size(max = 50)
    private String experienceLevel; // ENTRY, MID, SENIOR
    
    // Constructors
    public CreateJobRequest() {}
    
    public CreateJobRequest(String title, String description, String company, String location) {
        this.title = title;
        this.description = description;
        this.company = company;
        this.location = location;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public BigDecimal getSalary() {
        return salary;
    }
    
    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
    
    public String getJobType() {
        return jobType;
    }
    
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
    
    public String getExperienceLevel() {
        return experienceLevel;
    }
    
    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
}
