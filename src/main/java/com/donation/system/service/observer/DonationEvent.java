package com.donation.system.service.observer;

import com.donation.system.model.entity.Donation;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when a donation is recorded.
 *
 * @author Team
 */
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
