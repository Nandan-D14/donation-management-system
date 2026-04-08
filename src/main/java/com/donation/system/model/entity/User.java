package com.donation.system.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Base class for Donor and Patient
 * @author Nandani (SRN 364)
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
}