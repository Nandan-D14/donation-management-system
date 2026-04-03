package com.donation.system.service;

import com.donation.system.model.entity.Donation;
import com.donation.system.repository.DonationRepository;
import com.donation.system.service.singleton.EventManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        Donation saved = donationRepo.save(donation);
        eventManager.logEvent(saved);
        return saved;
    }

    public Donation updateStatus(int id, String status) {
        return donationRepo.findById(id).map(donation -> {
            donation.updateStatus(status);
            return donationRepo.save(donation);
        }).orElse(null);
    }

    public int getEventCount() {
        return eventManager.getEventCount();
    }
}
