package com.donation.system.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Donor user subtype.
 *
 * @author Team
 */
@Entity
@DiscriminatorValue("DONOR")
@Getter
@Setter
@NoArgsConstructor
public class Donor extends User {

    @Column
    private String bloodType;

    @Column
    private String organType;

    @Column
    private Boolean availability = true;

    @Column
    private String status = "PENDING";
}
