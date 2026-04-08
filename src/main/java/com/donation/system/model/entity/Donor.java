package com.donation.system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("Donor")
@Getter
@Setter
public class Donor extends User {

    private String bloodType;
    private String organType;

    private boolean availability;
    private String status;
}