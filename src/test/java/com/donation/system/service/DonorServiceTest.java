package com.donation.system.service;

import com.donation.system.model.entity.Donation;
import com.donation.system.model.entity.Donor;
import com.donation.system.repository.DonationRepository;
import com.donation.system.repository.DonorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DonorService.
 *
 * @author Team
 */
@ExtendWith(MockitoExtension.class)
class DonorServiceTest {

    @Mock
    private DonorRepository donorRepository;

    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private DonorService donorService;

    private Donor donor;

    @BeforeEach
    void setup() {
        donor = new Donor();
        donor.setId(1);
        donor.setName("John Donor");
        donor.setMail("donor@example.com");
        donor.setPassword("pass123");
        donor.setBloodType("O+");
    }

    @Test
    void saveDonor_shouldSetRoleAndAvailability() {
        when(donorRepository.save(any(Donor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Donor saved = donorService.saveDonor(donor);

        assertNotNull(saved);
        assertEquals("DONOR", saved.getRole());
        assertTrue(saved.getAvailability());
        verify(donorRepository).save(donor);
    }

    @Test
    void recordDonation_shouldCreateDonationAndUpdateDonorStatus() {
        when(donorRepository.save(any(Donor.class))).thenReturn(donor);
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Donation donation = donorService.recordDonation(donor, "BLOOD", 1);

        assertNotNull(donation);
        assertEquals("BLOOD", donation.getDonationType());
        assertEquals(1, donation.getQuantity());
        assertEquals("SUBMITTED", donation.getStatus());
        assertEquals(donor, donation.getDonor());
    }

    @Test
    void getDonationsForDonor_shouldReturnDonorDonations() {
        Donation donation = new Donation();
        donation.setDonor(donor);
        when(donationRepository.findByDonor(donor)).thenReturn(List.of(donation));

        List<Donation> donations = donorService.getDonationsForDonor(donor);

        assertEquals(1, donations.size());
        assertEquals(donor, donations.get(0).getDonor());
    }
}
