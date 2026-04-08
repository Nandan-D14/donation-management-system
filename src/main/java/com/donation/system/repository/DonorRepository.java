package com.donation.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.donation.system.model.entity.Donor;

public interface DonorRepository extends JpaRepository<Donor, Long> {
}