package com.donation.system.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private String requestType;

    @Column(nullable = false)
    private String requestDetails;

    @Column(length = 400)
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Request() {
    }

    public Request(User createdBy, String requestType, String requestDetails, String notes) {
        this.createdBy = createdBy;
        this.requestType = requestType;
        this.requestDetails = requestDetails;
        this.notes = notes;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getRequestDetails() {
        return requestDetails;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
