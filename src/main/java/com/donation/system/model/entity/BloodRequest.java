package com.donation.system.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("BLOOD")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BloodRequest extends Request {

    private String bloodGroup;

    public BloodRequest(User createdBy, int quantity, String bloodGroup) {
        super(createdBy, quantity, "BLOOD");
        this.bloodGroup = bloodGroup;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    @Override
    public String getRequestDetail() {
        return bloodGroup;
    }
}
