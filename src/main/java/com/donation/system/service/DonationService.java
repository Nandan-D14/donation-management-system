package com.donation.system.service;

import com.donation.system.model.entity.Donation;
import com.donation.system.repository.DonationRepository;
import com.donation.system.service.singleton.EventManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Donation service for admin reports and status updates.
 *
 * @author Team
 */
@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final EventManager eventManager;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
        this.eventManager = EventManager.getInstance();
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    public Optional<Donation> getDonationById(int id) {
        return donationRepository.findById(id);
    }

    public Donation recordDonation(Donation donation) {
        Donation savedDonation = donationRepository.save(donation);
        eventManager.logEvent(savedDonation);
        return savedDonation;
    }

    public Donation updateStatus(int id, String status) {
        return donationRepository.findById(id)
                .map(donation -> {
                    donation.updateStatus(status);
                    Donation updated = donationRepository.save(donation);
                    eventManager.logEvent(updated);
                    return updated;
                })
                .orElse(null);
    }

    public long getDonationCount() {
        return donationRepository.count();
    }

    public int getEventCount() {
        return eventManager.getEventCount();
    }
}
