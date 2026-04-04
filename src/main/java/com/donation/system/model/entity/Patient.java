package com.donation.system.model.entity;

import com.donation.system.service.factory.RequestFactory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int patientID;

    private String name;

    @Column(nullable = false, unique = true)
    private String mail;

    private String conditionNote;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User userAccount;

    private User ensureUserAccount() {
        if (userAccount == null) {
            userAccount = new User();
        }
        userAccount.setName(name);
        userAccount.setMail(mail);
        userAccount.setRole("PATIENT");
        return userAccount;
    }

    /**
     * GRASP Creator: Patient creates blood request objects via the factory.
     */
    public Request createBloodRequest(String bloodGroup, int quantity) {
        return RequestFactory.createBloodRequest(ensureUserAccount(), bloodGroup, quantity);
    }

    /**
     * GRASP Creator: Patient creates organ request objects via the factory.
     */
    public Request createOrganRequest(String organType, int quantity) {
        return RequestFactory.createOrganRequest(ensureUserAccount(), organType, quantity);
    }
}
