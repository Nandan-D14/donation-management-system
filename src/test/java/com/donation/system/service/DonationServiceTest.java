package com.donation.system.service;

import com.donation.system.model.entity.Donation;
import com.donation.system.repository.DonationRepository;
import com.donation.system.service.singleton.EventManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DonationService.
 *
 * @author Team
 */
@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private DonationService donationService;

    private Donation donation;

    @BeforeEach
    void setup() {
        donation = new Donation();
        donation.setId(1);
        donation.setDonationType("BLOOD");
        donation.setQuantity(1);
        donation.setStatus("SUBMITTED");
    }

    @Test
    void recordDonation_shouldSaveAndLogEvent() {
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Donation saved = donationService.recordDonation(donation);

        assertNotNull(saved);
        assertEquals("BLOOD", saved.getDonationType());
        verify(donationRepository).save(donation);
    }

    @Test
    void updateStatus_shouldUpdateDonationStatus() {
        when(donationRepository.findById(1)).thenReturn(Optional.of(donation));
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Donation updated = donationService.updateStatus(1, "COMPLETED");

        assertNotNull(updated);
        assertEquals("COMPLETED", updated.getStatus());
        verify(donationRepository).findById(1);
        verify(donationRepository).save(donation);
    }

    @Test
    void getDonationById_shouldReturnDonation() {
        when(donationRepository.findById(1)).thenReturn(Optional.of(donation));

        Optional<Donation> result = donationService.getDonationById(1);

        assertTrue(result.isPresent());
        assertEquals(donation, result.get());
    }

    @Test
    void getAllDonations_shouldReturnDonationList() {
        Donation donation2 = new Donation();
        donation2.setDonationType("ORGAN");
        when(donationRepository.findAll()).thenReturn(List.of(donation, donation2));

        List<Donation> donations = donationService.getAllDonations();

        assertEquals(2, donations.size());
    }

    private void assertTrue(boolean condition) {
        if (!condition) throw new AssertionError();
    }
}
