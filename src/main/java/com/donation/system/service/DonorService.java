package com.donation.system.service;

import org.springframework.stereotype.Service;
import java.util.Optional;

import com.donation.system.model.entity.Donor;
import com.donation.system.repository.DonorRepository;

/** GRASP: Information Expert */
@Service
public class DonorService {

    private final DonorRepository donorRepository;

    public DonorService(DonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }

    public Donor registerDonor(Donor donor) {
        donor.setAvailability(true);
        return donorRepository.save(donor);
    }

    public Optional<Donor> getDonor(Long id) {
        return donorRepository.findById(id);
    }
}