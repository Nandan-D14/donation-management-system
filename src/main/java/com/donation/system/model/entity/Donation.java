package com.donation.system.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "donations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int donationID;

    private String donationType;
    private Date date;
    private int quantity;
    private String status;

    @ManyToOne
    @JoinColumn(name = "donor_id")
    private User donor;

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }
}
