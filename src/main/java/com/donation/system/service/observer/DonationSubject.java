package com.donation.system.service.observer;

import com.donation.system.model.entity.Donation;

public interface DonationSubject {
    void registerObserver(DonationObserver observer);
    void removeObserver(DonationObserver observer);
    void notifyObservers(Donation donation);
}