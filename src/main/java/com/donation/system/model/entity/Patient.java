package com.donation.system.model.entity;

import jakarta.persistence.*;
import lombok.*;

import com.donation.system.service.observer.*;

/**
 * Patient Entity (Observer)
 */
@Entity
@DiscriminatorValue("Patient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends User implements DonationObserver {

    @Column(name = "required_blood_type")
    private String requiredBloodType;

    @Column(name = "required_organ")
    private String requiredOrgan;

    @Column(name = "urgency_level")
    private String urgencyLevel;

    @Column(name = "status")
    private String status;

    @Override
    public void onDonationAvailable(Donation donation) {
        this.status = "MATCH FOUND";
    }
}