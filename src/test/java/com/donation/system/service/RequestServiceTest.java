package com.donation.system.service;

import com.donation.system.model.entity.BloodRequest;
import com.donation.system.model.entity.Patient;
import com.donation.system.model.entity.Request;
import com.donation.system.repository.RequestRepository;
import com.donation.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for RequestService.
 *
 * @author Team
 */
@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RequestService requestService;

    private Patient patient;

    @BeforeEach
    void setup() {
        patient = new Patient();
        patient.setId(1);
        patient.setName("Nandani");
        patient.setMail("nandani@example.com");
        patient.setPassword("pass123");
        patient.setRole("PATIENT");
    }

    @Test
    void createRequest_shouldSaveBloodRequest() {
        when(userRepository.findByMail("nandani@example.com")).thenReturn(Optional.of(patient));
        when(userRepository.save(any())).thenReturn(patient);
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Request request = requestService.createRequest(
                "PATIENT",
                "BLOOD",
                "Nandani",
                "nandani@example.com",
                "A+",
                2
        );

        assertNotNull(request);
        assertEquals("BLOOD", request.getRequestType());
        assertEquals(2, request.getQuantity());
    }

    @Test
    void createRequest_shouldThrowForInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> requestService.createRequest(
                "PATIENT",
                "BLOOD",
                "Nandani",
                "nandani@example.com",
                "A+",
                0
        ));
    }

    @Test
    void updateRequestStatus_shouldAllowPendingToApproved() {
        BloodRequest request = new BloodRequest(patient, 2, "A+");
        request.setStatus("PENDING");

        when(requestRepository.findById(1)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean updated = requestService.updateRequestStatus(1, "APPROVED");

        assertTrue(updated);
        assertEquals("APPROVED", request.getStatus());
        verify(requestRepository).save(request);
    }

    @Test
    void updateRequestStatus_shouldRejectTerminalTransition() {
        BloodRequest request = new BloodRequest(patient, 1, "O+");
        request.setStatus("APPROVED");

        when(requestRepository.findById(anyInt())).thenReturn(Optional.of(request));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> requestService.updateRequestStatus(99, "DENIED"));

        assertTrue(ex.getMessage().contains("Invalid request status transition"));
    }
}
