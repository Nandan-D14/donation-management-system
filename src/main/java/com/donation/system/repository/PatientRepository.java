package com.donation.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.donation.system.model.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}