package com.donation.system.service;

import com.donation.system.model.entity.Donation;
import com.donation.system.model.entity.Donor;
import com.donation.system.repository.DonationRepository;
import com.donation.system.repository.DonorRepository;
import com.donation.system.service.singleton.EventManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Donor workflow service.
 *
 * @author Team
 */
@Service
public class DonorService {

    private final DonorRepository donorRepository;
    private final DonationRepository donationRepository;
    private final EventManager eventManager;

    public DonorService(DonorRepository donorRepository, DonationRepository donationRepository) {
        this.donorRepository = donorRepository;
        this.donationRepository = donationRepository;
        this.eventManager = EventManager.getInstance();
    }

    public Donor saveDonor(Donor donor) {
        donor.setRole("DONOR");
        if (donor.getAvailability() == null) {
            donor.setAvailability(true);
        }
        if (donor.getStatus() == null || donor.getStatus().isBlank()) {
            donor.setStatus("PENDING");
        }
        return donorRepository.save(donor);
    }

    public Donation recordDonation(Donor donor, String donationType, String bloodType, String organType, int quantity) {
        Donation donation = new Donation();
        donation.setDonor(donor);
        donation.setDonationType(donationType.toUpperCase());
        donation.setBloodType(bloodType != null ? bloodType.toUpperCase() : null);
        donation.setOrganType(organType != null ? organType.toUpperCase() : null);
        donation.setQuantity(quantity);
        donation.setDate(LocalDateTime.now());
        donation.setStatus("SUBMITTED");
        donation.setAllocatedStatus("NOT_ALLOCATED");

        donor.setStatus("DONATED");
        donorRepository.save(donor);

        Donation saved = donationRepository.save(donation);
        eventManager.logEvent(saved);
        return saved;
    }

    public Donation recordDonation(Donor donor, String donationType, int quantity) {
        return recordDonation(donor, donationType, null, null, quantity);
    }

    public List<Donation> getDonationsForDonor(Donor donor) {
        return donationRepository.findByDonor(donor);
    }
}
