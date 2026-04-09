package com.donation.system.service;

import com.donation.system.model.entity.BloodRequest;
import com.donation.system.model.entity.Inventory;
import com.donation.system.model.entity.Patient;
import com.donation.system.model.entity.Request;
import com.donation.system.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for InventoryService matching logic.
 *
 * @author Team
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void canFulfill_shouldReturnTrueWhenBloodMatchesAndEnoughStock() {
        Patient patient = new Patient();
        patient.setRole("PATIENT");
        Request request = new BloodRequest(patient, 1, "A+");

        Inventory inventory = new Inventory();
        inventory.setBloodType("A+");
        inventory.setQuantity(5);

        when(inventoryRepository.findAll()).thenReturn(List.of(inventory));

        assertTrue(inventoryService.canFulfill(request));
    }

    @Test
    void canFulfill_shouldReturnFalseWhenNoStock() {
        Patient patient = new Patient();
        patient.setRole("PATIENT");
        Request request = new BloodRequest(patient, 3, "B+");

        Inventory inventory = new Inventory();
        inventory.setBloodType("B+");
        inventory.setQuantity(1);

        when(inventoryRepository.findAll()).thenReturn(List.of(inventory));

        assertFalse(inventoryService.canFulfill(request));
    }
}
