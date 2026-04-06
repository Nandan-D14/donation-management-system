package com.donation.system.model.entity;

import com.donation.system.service.factory.RequestFactory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    protected User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // GRASP Creator: user creates request through factory.
    public Request createBloodRequest(RequestFactory factory, String bloodType, Integer unitsRequested, String notes) {
        return factory.createBloodRequest(this, bloodType, unitsRequested, notes);
    }

    // GRASP Creator: user creates request through factory.
    public Request createOrganRequest(RequestFactory factory, String organType, String notes) {
        return factory.createOrganRequest(this, organType, notes);
    }
}
