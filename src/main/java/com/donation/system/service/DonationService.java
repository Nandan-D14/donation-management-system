package com.donation.system.service;

import com.donation.system.model.entity.Donation;
import com.donation.system.repository.DonationRepository;
import com.donation.system.service.singleton.EventManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * DonationService handles donation-related data access and persistence.
 *
 * @author Nandan (SRN 363)
 */
@Service
public class DonationService {

    private final DonationRepository donationRepo;
    private final EventManager eventManager;

    public DonationService(DonationRepository donationRepo) {
        this.donationRepo = donationRepo;
        this.eventManager = EventManager.getInstance();
    }

    public List<Donation> getAllDonations() {
        return donationRepo.findAll();
    }

    public Optional<Donation> getDonationById(int id) {
        return donationRepo.findById(id);
    }

    public List<Donation> getDonationsByStatus(String status) {
        return donationRepo.findByStatus(status);
    }

    public Donation recordDonation(Donation donation) {
        Donation savedDonation = donationRepo.save(donation);
        eventManager.logEvent(savedDonation);
        return savedDonation;
    }

    public Donation updateStatus(int id, String status) {
        return donationRepo.findById(id).map(donation -> {
            donation.updateStatus(status);
            Donation updatedDonation = donationRepo.save(donation);
            eventManager.logEvent(updatedDonation);
            return updatedDonation;
        }).orElse(null);
    }

    public long getDonationCount() {
        return donationRepo.count();
    }

    public int getEventCount() {
        return eventManager.getEventCount();
    }
}
