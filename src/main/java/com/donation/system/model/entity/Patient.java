package com.donation.system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("Patient")
@Getter
@Setter
public class Patient extends User {

    private String requiredBloodType;
    private String requiredOrgan;
    private String urgencyLevel;

    private String status;
}