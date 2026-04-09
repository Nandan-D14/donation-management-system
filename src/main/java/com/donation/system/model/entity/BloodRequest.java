package com.donation.system.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Blood request subtype.
 *
 * @author Team
 */
@Entity
@DiscriminatorValue("BLOOD")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class BloodRequest extends Request {

    private String bloodGroup;

    public BloodRequest(User createdBy, int quantity, String bloodGroup) {
        super(createdBy, quantity, "BLOOD");
        this.bloodGroup = bloodGroup;
    }

    @Override
    public String getRequestDetail() {
        return bloodGroup;
    }
}
