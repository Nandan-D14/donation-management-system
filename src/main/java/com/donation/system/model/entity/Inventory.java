package com.donation.system.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Inventory entity — standalone, no inheritance from User.
 * GRASP: Low Coupling — Inventory does NOT depend on Donor, Patient, or Admin entities.
 *
 * @author Neha (SRN 379)
 */
@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "blood_type", length = 10)
    private String bloodType;

    @Column(name = "organ_type", length = 50)
    private String organType;

    @Column(nullable = false)
    private Integer quantity = 0;

    /** Add stock — increases quantity by given amount. */
    public void addStock(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    /**
     * Reduce stock — decreases quantity if sufficient stock exists.
     * @return true if reduction succeeded, false if insufficient stock.
     */
    public boolean reduceStock(int amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
            return true;
        }
        return false;
    }

    /**
     * Check availability — returns true if at least the requested amount is in stock.
     */
    public boolean checkAvailability(int amount) {
        return this.quantity >= amount;
    }
}
