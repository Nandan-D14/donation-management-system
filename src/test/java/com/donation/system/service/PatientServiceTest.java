package com.donation.system.service;

import com.donation.system.model.entity.Patient;
import com.donation.system.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PatientService.
 *
 * @author Team
 */
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setup() {
        patient = new Patient();
        patient.setId(1);
        patient.setName("Jane Patient");
        patient.setMail("patient@example.com");
        patient.setPassword("pass123");
        patient.setConditionNote("Need blood urgently");
    }

    @Test
    void registerPatient_shouldSetRoleAndStatus() {
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Patient saved = patientService.registerPatient(patient);

        assertNotNull(saved);
        assertEquals("PATIENT", saved.getRole());
        assertEquals("PENDING", saved.getStatus());
        verify(patientRepository).save(patient);
    }

    @Test
    void getAllPatients_shouldReturnPatientList() {
        Patient patient2 = new Patient();
        patient2.setName("Another Patient");
        when(patientRepository.findAll()).thenReturn(List.of(patient, patient2));

        List<Patient> patients = patientService.getAllPatients();

        assertEquals(2, patients.size());
    }
}
