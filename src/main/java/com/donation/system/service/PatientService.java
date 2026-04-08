package com.donation.system.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.donation.system.model.entity.Patient;
import com.donation.system.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository repo;

    public PatientService(PatientRepository repo) {
        this.repo = repo;
    }

    public Patient registerPatient(Patient p) {
        p.setStatus("PENDING");
        return repo.save(p);
    }

    public List<Patient> getAllRequests() {
        return repo.findAll();
    }
}