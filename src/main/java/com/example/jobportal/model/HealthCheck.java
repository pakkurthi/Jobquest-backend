package com.example.jobportal.model;

import jakarta.persistence.*;

@Entity
public class HealthCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    public HealthCheck() {}

    public HealthCheck(String status) {
        this.status = status;
    }

    // Getters & setters
    public Long getId() { return id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
