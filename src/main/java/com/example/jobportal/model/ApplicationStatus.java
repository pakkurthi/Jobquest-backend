package com.example.jobportal.model;

public enum ApplicationStatus {
    APPLIED,        // Initial application submitted
    UNDER_REVIEW,   // Application being reviewed by employer
    SHORTLISTED,    // Candidate has been shortlisted
    INTERVIEWED,    // Interview has been conducted
    OFFERED,        // Job offer extended
    ACCEPTED,       // Offer accepted by candidate
    REJECTED,       // Application rejected
    WITHDRAWN       // Application withdrawn by candidate
}
