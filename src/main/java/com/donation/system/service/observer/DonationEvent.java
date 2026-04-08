package com.donation.system.service.observer;

import org.springframework.context.ApplicationEvent;
import com.donation.system.model.entity.Donor;

public class DonationEvent extends ApplicationEvent {

    private final Donor donor;

    public DonationEvent(Object source, Donor donor) {
        super(source);
        this.donor = donor;
    }

    public Donor getDonor() {
        return donor;
    }
}