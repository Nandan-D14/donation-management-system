package com.donation.system.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Base request type for blood and organ requests.
 *
 * @author Team
 */
@Entity
@Table(name = "requests")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "request_kind")
@Getter
@Setter
@NoArgsConstructor
public abstract class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(nullable = false)
    private String requestType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
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
