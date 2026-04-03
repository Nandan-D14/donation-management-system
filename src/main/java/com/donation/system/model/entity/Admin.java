package com.donation.system.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@DiscriminatorValue("Admin")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends User {

    private int adminID;

    public void manageUsers() {
        System.out.println("Admin managing users");
    }

    public void generateReports() {
        System.out.println("Admin generating reports");
    }
}
