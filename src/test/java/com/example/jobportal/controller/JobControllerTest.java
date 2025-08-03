package com.example.jobportal.controller;

import com.example.jobportal.dto.CreateJobRequest;
import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.dto.UpdateJobRequest;
import com.example.jobportal.service.JobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobController.class)
public class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateJobRequest createJobRequest;
    private UpdateJobRequest updateJobRequest;
    private JobResponse jobResponse;

    @BeforeEach
    void setUp() {
        createJobRequest = new CreateJobRequest();
        createJobRequest.setTitle("Senior Software Engineer");
        createJobRequest.setDescription("We are looking for a senior software engineer with 5+ years of experience.");
        createJobRequest.setCompany("Tech Corp");
        createJobRequest.setLocation("San Francisco, CA");
        createJobRequest.setSalary(new BigDecimal("120000"));
        createJobRequest.setJobType("FULL_TIME");
        createJobRequest.setExperienceLevel("SENIOR");

        updateJobRequest = new UpdateJobRequest();
        updateJobRequest.setTitle("Senior Software Engineer (Updated)");
        updateJobRequest.setDescription("Updated job description");
        updateJobRequest.setCompany("Tech Corp");
        updateJobRequest.setLocation("San Francisco, CA");
        updateJobRequest.setSalary(new BigDecimal("130000"));
        updateJobRequest.setJobType("FULL_TIME");
        updateJobRequest.setExperienceLevel("SENIOR");

        jobResponse = new JobResponse();
        jobResponse.setId(1L);
        jobResponse.setTitle("Senior Software Engineer");
        jobResponse.setDescription("We are looking for a senior software engineer with 5+ years of experience.");
        jobResponse.setCompany("Tech Corp");
        jobResponse.setLocation("San Francisco, CA");
        jobResponse.setSalary(new BigDecimal("120000"));
        jobResponse.setJobType("FULL_TIME");
        jobResponse.setExperienceLevel("SENIOR");
        jobResponse.setCreatedAt(LocalDateTime.now());
        jobResponse.setIsActive(true);
        jobResponse.setCreatedById(1L);
        jobResponse.setCreatedByName("John Employer");
        jobResponse.setCreatedByEmail("john.employer@example.com");
    }

    @Test
    @WithMockUser(roles = "JOB_PROVIDER")
    void testCreateJob_Success() throws Exception {
        when(jobService.createJob(any(CreateJobRequest.class))).thenReturn(jobResponse);

        mockMvc.perform(post("/api/jobs/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createJobRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Senior Software Engineer"))
                .andExpect(jsonPath("$.company").value("Tech Corp"))
                .andExpect(jsonPath("$.location").value("San Francisco, CA"))
                .andExpect(jsonPath("$.salary").value(120000))
                .andExpect(jsonPath("$.jobType").value("FULL_TIME"))
                .andExpect(jsonPath("$.experienceLevel").value("SENIOR"))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(jobService, times(1)).createJob(any(CreateJobRequest.class));
    }

    @Test
    @WithMockUser(roles = "JOB_PROVIDER")
    void testCreateJob_ValidationError() throws Exception {
        CreateJobRequest invalidRequest = new CreateJobRequest();
        invalidRequest.setTitle(""); // Empty title should fail validation
        invalidRequest.setDescription(""); // Empty description should fail validation

        mockMvc.perform(post("/api/jobs/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(jobService, never()).createJob(any(CreateJobRequest.class));
    }

    @Test
    void testCreateJob_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/jobs/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createJobRequest)))
                .andExpect(status().isUnauthorized());

        verify(jobService, never()).createJob(any(CreateJobRequest.class));
    }

    @Test
    @WithMockUser(roles = "JOB_SEEKER") // Wrong role
    void testCreateJob_Forbidden() throws Exception {
        mockMvc.perform(post("/api/jobs/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createJobRequest)))
                .andExpect(status().isForbidden());

        verify(jobService, never()).createJob(any(CreateJobRequest.class));
    }

    @Test
    @WithMockUser(roles = "JOB_PROVIDER")
    void testUpdateJob_Success() throws Exception {
        JobResponse updatedResponse = new JobResponse();
        updatedResponse.setId(1L);
        updatedResponse.setTitle("Senior Software Engineer (Updated)");
        updatedResponse.setSalary(new BigDecimal("130000"));

        when(jobService.updateJob(eq(1L), any(UpdateJobRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/jobs/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateJobRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Senior Software Engineer (Updated)"))
                .andExpect(jsonPath("$.salary").value(130000));

        verify(jobService, times(1)).updateJob(eq(1L), any(UpdateJobRequest.class));
    }

    @Test
    @WithMockUser(roles = "JOB_PROVIDER")
    void testUpdateJob_NotFound() throws Exception {
        when(jobService.updateJob(eq(999L), any(UpdateJobRequest.class)))
                .thenThrow(new RuntimeException("Job not found"));

        mockMvc.perform(put("/api/jobs/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateJobRequest)))
                .andExpect(status().isInternalServerError());

        verify(jobService, times(1)).updateJob(eq(999L), any(UpdateJobRequest.class));
    }

    @Test
    @WithMockUser(roles = "JOB_PROVIDER")
    void testDeleteJob_Success() throws Exception {
        doNothing().when(jobService).deleteJob(1L);

        mockMvc.perform(delete("/api/jobs/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Job deleted successfully"));

        verify(jobService, times(1)).deleteJob(1L);
    }

    @Test
    @WithMockUser(roles = "JOB_PROVIDER")
    void testDeleteJob_NotFound() throws Exception {
        doThrow(new RuntimeException("Job not found")).when(jobService).deleteJob(999L);

        mockMvc.perform(delete("/api/jobs/999")
                .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(jobService, times(1)).deleteJob(999L);
    }

    @Test
    @WithMockUser(roles = "JOB_PROVIDER")
    void testGetMyJobs_Success() throws Exception {
        List<JobResponse> jobs = Arrays.asList(jobResponse);
        when(jobService.getMyJobs()).thenReturn(jobs);

        mockMvc.perform(get("/api/jobs/my-jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Senior Software Engineer"));

        verify(jobService, times(1)).getMyJobs();
    }

    @Test
    @WithMockUser(roles = "JOB_PROVIDER")
    void testGetMyJobsCount_Success() throws Exception {
        when(jobService.getMyJobsCount()).thenReturn(5L);

        mockMvc.perform(get("/api/jobs/my-jobs/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5L));

        verify(jobService, times(1)).getMyJobsCount();
    }

    // Public API Tests - No authentication required

    @Test
    void testGetAllActiveJobs_Success() throws Exception {
        List<JobResponse> jobs = Arrays.asList(jobResponse);
        when(jobService.getActiveJobs()).thenReturn(jobs);

        mockMvc.perform(get("/api/jobs/public/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Senior Software Engineer"));

        verify(jobService, times(1)).getActiveJobs();
    }

    @Test
    void testGetJobById_Success() throws Exception {
        when(jobService.getJobById(1L)).thenReturn(jobResponse);

        mockMvc.perform(get("/api/jobs/public/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Senior Software Engineer"));

        verify(jobService, times(1)).getJobById(1L);
    }

    @Test
    void testGetJobById_NotFound() throws Exception {
        when(jobService.getJobById(999L)).thenThrow(new RuntimeException("Job not found"));

        mockMvc.perform(get("/api/jobs/public/999"))
                .andExpect(status().isInternalServerError());

        verify(jobService, times(1)).getJobById(999L);
    }

    @Test
    void testSearchJobs_Success() throws Exception {
        List<JobResponse> jobs = Arrays.asList(jobResponse);
        when(jobService.searchJobs("software")).thenReturn(jobs);

        mockMvc.perform(get("/api/jobs/public/search")
                .param("keyword", "software"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Senior Software Engineer"));

        verify(jobService, times(1)).searchJobs("software");
    }

    @Test
    void testGetJobsByLocation_Success() throws Exception {
        List<JobResponse> jobs = Arrays.asList(jobResponse);
        when(jobService.getJobsByLocation("San Francisco")).thenReturn(jobs);

        mockMvc.perform(get("/api/jobs/public/location")
                .param("location", "San Francisco"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].location").value("San Francisco, CA"));

        verify(jobService, times(1)).getJobsByLocation("San Francisco");
    }

    @Test
    void testGetJobsByType_Success() throws Exception {
        List<JobResponse> jobs = Arrays.asList(jobResponse);
        when(jobService.getJobsByType("FULL_TIME")).thenReturn(jobs);

        mockMvc.perform(get("/api/jobs/public/type")
                .param("jobType", "FULL_TIME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].jobType").value("FULL_TIME"));

        verify(jobService, times(1)).getJobsByType("FULL_TIME");
    }

    @Test
    void testGetJobsByExperienceLevel_Success() throws Exception {
        List<JobResponse> jobs = Arrays.asList(jobResponse);
        when(jobService.getJobsByExperienceLevel("SENIOR")).thenReturn(jobs);

        mockMvc.perform(get("/api/jobs/public/experience")
                .param("experienceLevel", "SENIOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].experienceLevel").value("SENIOR"));

        verify(jobService, times(1)).getJobsByExperienceLevel("SENIOR");
    }
}
