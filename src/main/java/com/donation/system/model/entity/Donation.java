package com.donation.system.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Donation record created by a donor.
 *
 * @author Team
 */
@Entity
@Table(name = "donations")
@Getter
@Setter
@NoArgsConstructor
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(nullable = false)
    private String donationType;

    @Column(name = "blood_type")
    private String bloodType;

    @Column(name = "organ_type")
    private String organType;

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(nullable = true)
    private String allocatedStatus = "NOT_ALLOCATED";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id")
    private Donor donor;

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }
}
