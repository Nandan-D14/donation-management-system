package com.donation.system.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.donation.system.model.entity.Patient;
import com.donation.system.repository.PatientRepository;

/**
 * GRASP: Information Expert
 * Handles all Patient-related business logic
 * @author Nandani
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // 🔥 Register patient request
    public Patient registerPatient(Patient patient) {
        patient.setStatus("PENDING"); // default status
        return patientRepository.save(patient);
    }

    // 🔍 Get single patient
    public Optional<Patient> getPatient(Long id) {
        return patientRepository.findById(id);
    }

    // 📊 Get all requests (for track page)
    public List<Patient> getAllRequests() {
        return patientRepository.findAll();
    }

    // 🔄 Update status (future use: match/approve)
    public void updateStatus(Long id, String status) {
        Optional<Patient> optionalPatient = patientRepository.findById(id);

        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            patient.setStatus(status);
            patientRepository.save(patient);
        }
    }
}