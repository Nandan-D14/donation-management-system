package com.donation.system.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Base user entity for authentication and role-based access.
 *
 * @author Team
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
@NoArgsConstructor
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String mail;

    @Column(nullable = false)
    private String password;

    @Column
    private Long phone;

    @Column(nullable = false)
    private String role;

    public boolean login(String inputMail, String inputPassword) {
        return Objects.equals(mail, inputMail) && Objects.equals(password, inputPassword);
    }

    public void register() {
        if (role == null || role.isBlank()) {
            role = "USER";
        }
    }

    public void updateProfile(String updatedName, Long updatedPhone) {
        this.name = updatedName;
        this.phone = updatedPhone;
    }

    public Request createBloodRequest(String bloodGroup, int quantity) {
        return new BloodRequest(this, quantity, bloodGroup);
    }

    public Request createOrganRequest(String organType, int quantity) {
        return new OrganRequest(this, quantity, organType);
    }
}
