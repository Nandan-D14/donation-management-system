package com.donation.system.service.observer;

import org.springframework.context.ApplicationEvent;
import com.donation.system.model.entity.Donation;

public class DonationEvent extends ApplicationEvent {

    private final Donation donation;

    public DonationEvent(Object source, Donation donation) {
        super(source);
        this.donation = donation;
    }

    public Donation getDonation() {
        return donation;
    }
}