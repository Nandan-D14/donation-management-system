package com.donation.system.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Patient user subtype.
 *
 * @author Team
 */
@Entity
@DiscriminatorValue("PATIENT")
@Getter
@Setter
@NoArgsConstructor
public class Patient extends User {

    @Column
    private String conditionNote;

    @Column
    private String status = "PENDING";
}
