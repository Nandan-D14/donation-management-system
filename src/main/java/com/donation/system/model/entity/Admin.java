package com.donation.system.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int adminID;

    private String name;
    private String mail;
    private String password;
    private int phone;

    public void manageUsers() {
        System.out.println("Admin managing users");
    }

    public void generateReports() {
        System.out.println("Admin generating reports");
    }
}
