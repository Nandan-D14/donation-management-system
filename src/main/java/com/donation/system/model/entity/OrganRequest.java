package com.donation.system.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ORGAN")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrganRequest extends Request {

    private String organType;

    public OrganRequest(User createdBy, int quantity, String organType) {
        super(createdBy, quantity, "ORGAN");
        this.organType = organType;
    }

    public String getOrganType() {
        return organType;
    }

    public void setOrganType(String organType) {
        this.organType = organType;
    }

    @Override
    public String getRequestDetail() {
        return organType;
    }
}
