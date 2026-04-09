package com.donation.system.service.observer;

import com.donation.system.model.entity.Donation;

public interface DonationObserver {
    void onDonationAvailable(Donation donation);
}