package com.donation.system.repository;

import com.donation.system.model.entity.Donation;
import com.donation.system.model.entity.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Integer> {
    List<Donation> findByStatus(String status);
    List<Donation> findByDonationType(String donationType);
    List<Donation> findByDonor(Donor donor);
}
