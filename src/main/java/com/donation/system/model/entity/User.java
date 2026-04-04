package com.donation.system.model.entity;

import com.donation.system.service.factory.RequestFactory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "request_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userID;

    private String name;

    @Column(nullable = false, unique = true)
    private String mail;

    private String role;

    /**
     * GRASP Creator: User creates blood request objects via the factory.
     */
    public Request createBloodRequest(String bloodGroup, int quantity) {
        return RequestFactory.createBloodRequest(this, bloodGroup, quantity);
    }

    /**
     * GRASP Creator: User creates organ request objects via the factory.
     */
    public Request createOrganRequest(String organType, int quantity) {
        return RequestFactory.createOrganRequest(this, organType, quantity);
    }
}
