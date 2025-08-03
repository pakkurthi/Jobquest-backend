package com.example.jobportal.dto;

import com.example.jobportal.model.ApplicationStatus;

public class UpdateApplicationStatusRequest {
    private ApplicationStatus status;

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}
