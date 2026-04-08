package com.donation.system.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

import com.donation.system.service.observer.*;

@Entity
@DiscriminatorValue("Donor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Donor extends User implements DonationSubject {

    private String bloodType;
    private String organType;
    private boolean availability;

    @Transient
    private List<DonationObserver> observers = new ArrayList<>();

    public void donateBlood() {
        this.availability = false;
    }

    public void donateOrgan() {
        this.availability = false;
    }

    public void viewDonationStatus() {
        System.out.println("Availability: " + availability);
    }

    @Override
    public void registerObserver(DonationObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(DonationObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Donation donation) {
        for (DonationObserver obs : observers) {
            obs.onDonationAvailable(donation);
        }
    }
}