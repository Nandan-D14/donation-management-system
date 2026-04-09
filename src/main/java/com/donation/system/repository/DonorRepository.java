package com.donation.system.repository;

import com.donation.system.model.entity.Donor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DonorRepository extends JpaRepository<Donor, Integer> {
    Optional<Donor> findByMail(String mail);
}
