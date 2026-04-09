package com.donation.system.service;

import com.donation.system.model.entity.Patient;
import com.donation.system.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Patient workflow service.
 *
 * @author Team
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient registerPatient(Patient patient) {
        patient.setRole("PATIENT");
        if (patient.getStatus() == null || patient.getStatus().isBlank()) {
            patient.setStatus("PENDING");
        }
        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
}
