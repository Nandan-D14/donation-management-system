package com.donation.system.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Organ request subtype.
 *
 * @author Team
 */
@Entity
@DiscriminatorValue("ORGAN")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OrganRequest extends Request {

    private String organType;

    public OrganRequest(User createdBy, int quantity, String organType) {
        super(createdBy, quantity, "ORGAN");
        this.organType = organType;
    }

    @Override
    public String getRequestDetail() {
        return organType;
    }
}
