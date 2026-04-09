package com.donation.system.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

/**
 * Admin user subtype.
 *
 * @author Team
 */
@Entity
@DiscriminatorValue("ADMIN")
@NoArgsConstructor
public class Admin extends User {

    public void manageUsers() {
    }

    public void generateReports() {
    }
}
