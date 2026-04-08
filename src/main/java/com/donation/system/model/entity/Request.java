package com.donation.system.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_requests")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "request_kind")
@Data
@NoArgsConstructor
public abstract class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int requestID;

    private String requestType;
    private int quantity;
    private String status;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

    protected Request(User createdBy, int quantity, String requestType) {
        this.createdBy = createdBy;
        this.quantity = quantity;
        this.requestType = requestType;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    @Transient
    public abstract String getRequestDetail();
}
