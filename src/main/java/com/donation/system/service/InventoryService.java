package com.donation.system.service;

import com.donation.system.model.entity.Inventory;
import com.donation.system.model.entity.Request;
import com.donation.system.model.entity.Donation;
import com.donation.system.repository.InventoryRepository;
import com.donation.system.repository.DonationRepository;
import com.donation.system.repository.RequestRepository;
import com.donation.system.service.strategy.BloodMatchingStrategy;
import com.donation.system.service.strategy.MatchingStrategy;
import com.donation.system.service.strategy.OrganMatchingStrategy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles inventory updates and strategy-based request matching.
 *
 * @author Team
 */
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final DonationRepository donationRepository;
    private final RequestRepository requestRepository;
    private String matchingStrategy = "NORMAL";

    public InventoryService(InventoryRepository inventoryRepository,
                            DonationRepository donationRepository,
                            RequestRepository requestRepository) {
        this.inventoryRepository = inventoryRepository;
        this.donationRepository = donationRepository;
        this.requestRepository = requestRepository;
    }

    /**
     * Auto-sync inventory from all "SUBMITTED" donations.
     * Aggregates by blood/organ type and quantity.
     */
    public void syncInventoryFromDonations() {
        List<Donation> submissions = donationRepository.findByStatus("SUBMITTED");
        Map<String, Integer> bloodInventory = new HashMap<>();
        Map<String, Integer> organInventory = new HashMap<>();

        for (Donation d : submissions) {
            if ("BLOOD".equalsIgnoreCase(d.getDonationType()) && d.getBloodType() != null) {
                String type = d.getBloodType().toUpperCase();
                bloodInventory.put(type, bloodInventory.getOrDefault(type, 0) + d.getQuantity());
            } else if ("ORGAN".equalsIgnoreCase(d.getDonationType()) && d.getOrganType() != null) {
                String type = d.getOrganType().toUpperCase();
                organInventory.put(type, organInventory.getOrDefault(type, 0) + d.getQuantity());
            }
        }

        // Update blood inventory
        for (Map.Entry<String, Integer> entry : bloodInventory.entrySet()) {
            Optional<Inventory> existing = inventoryRepository.findByBloodTypeIgnoreCase(entry.getKey());
            if (existing.isPresent()) {
                existing.get().setQuantity(entry.getValue());
                inventoryRepository.save(existing.get());
            } else {
                Inventory inv = new Inventory();
                inv.setBloodType(entry.getKey());
                inv.setQuantity(entry.getValue());
                inventoryRepository.save(inv);
            }
        }

        // Update organ inventory
        for (Map.Entry<String, Integer> entry : organInventory.entrySet()) {
            Optional<Inventory> existing = inventoryRepository.findByOrganTypeIgnoreCase(entry.getKey());
            if (existing.isPresent()) {
                existing.get().setQuantity(entry.getValue());
                inventoryRepository.save(existing.get());
            } else {
                Inventory inv = new Inventory();
                inv.setOrganType(entry.getKey());
                inv.setQuantity(entry.getValue());
                inventoryRepository.save(inv);
            }
        }
    }

    public Inventory addOrUpdateInventory(Inventory inventory) {
        if (inventory.getQuantity() == null || inventory.getQuantity() < 0) {
            inventory.setQuantity(0);
        }
        if (inventory.getBloodType() != null) {
            inventory.setBloodType(inventory.getBloodType().trim().toUpperCase());
        }
        if (inventory.getOrganType() != null) {
            inventory.setOrganType(inventory.getOrganType().trim().toUpperCase());
        }
        return inventoryRepository.save(inventory);
    }

    public List<Inventory> getAllInventory() {
        syncInventoryFromDonations();
        return inventoryRepository.findAll();
    }

    public Inventory addStock(String bloodType, String organType, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        String normalizedBlood = bloodType == null || bloodType.isBlank() ? null : bloodType.trim().toUpperCase();
        String normalizedOrgan = organType == null || organType.isBlank() ? null : organType.trim().toUpperCase();

        Optional<Inventory> existing = normalizedBlood != null
                ? inventoryRepository.findByBloodTypeIgnoreCase(normalizedBlood)
                : inventoryRepository.findByOrganTypeIgnoreCase(normalizedOrgan);

        Inventory inventory = existing.orElseGet(Inventory::new);
        if (inventory.getBloodType() == null) {
            inventory.setBloodType(normalizedBlood);
        }
        if (inventory.getOrganType() == null) {
            inventory.setOrganType(normalizedOrgan);
        }
        if (inventory.getQuantity() == null) {
            inventory.setQuantity(0);
        }
        inventory.addStock(amount);
        return inventoryRepository.save(inventory);
    }

    public boolean reduceStock(String bloodType, String organType, int amount) {
        if (amount <= 0) {
            return false;
        }

        String normalizedBlood = bloodType == null || bloodType.isBlank() ? null : bloodType.trim().toUpperCase();
        String normalizedOrgan = organType == null || organType.isBlank() ? null : organType.trim().toUpperCase();

        Optional<Inventory> existing = normalizedBlood != null
                ? inventoryRepository.findByBloodTypeIgnoreCase(normalizedBlood)
                : inventoryRepository.findByOrganTypeIgnoreCase(normalizedOrgan);

        if (existing.isEmpty()) {
            return false;
        }
        Inventory inventory = existing.get();
        if (!inventory.reduceStock(amount)) {
            return false;
        }
        inventoryRepository.save(inventory);
        return true;
    }

    public void switchMatchingStrategy(String strategy) {
        if (strategy == null || strategy.isBlank()) {
            this.matchingStrategy = "NORMAL";
            return;
        }
        this.matchingStrategy = strategy.trim().toUpperCase();
    }

    public String getMatchingStrategy() {
        return matchingStrategy;
    }

    public List<Request> getRequestMatchingPreview() {
        List<Request> pending = requestRepository.findByStatusIgnoreCaseOrderByCreatedAtAsc("PENDING");
        if ("HIGH_PRIORITY".equalsIgnoreCase(matchingStrategy)) {
            return pending.stream()
                    .sorted(Comparator.comparing(Request::getQuantity).reversed())
                    .collect(Collectors.toList());
        }
        return pending;
    }

    /**
     * Allocate a donation to fulfill a request and reduce inventory.
     */
    public boolean allocateDonation(int donationId, int requestId, int quantity) {
        Optional<Donation> donOptional = donationRepository.findById(donationId);
        if (donOptional.isEmpty()) {
            return false;
        }

        Donation don = donOptional.get();
        String invQuery = "BLOOD".equalsIgnoreCase(don.getDonationType()) ? don.getBloodType() : don.getOrganType();
        
        Optional<Inventory> invOptional = "BLOOD".equalsIgnoreCase(don.getDonationType())
                ? inventoryRepository.findByBloodTypeIgnoreCase(invQuery)
                : inventoryRepository.findByOrganTypeIgnoreCase(invQuery);

        if (invOptional.isEmpty() || !invOptional.get().reduceStock(quantity)) {
            return false;
        }

        don.setAllocatedStatus("ALLOCATED");
        don.setStatus("ALLOCATED");
        donationRepository.save(don);
        inventoryRepository.save(invOptional.get());
        return true;
    }

    public boolean canFulfill(Request request) {
        MatchingStrategy strategy = "BLOOD".equalsIgnoreCase(request.getRequestType())
                ? new BloodMatchingStrategy()
                : new OrganMatchingStrategy();

        for (Inventory inventory : inventoryRepository.findAll()) {
            if (strategy.matches(inventory, request) && inventory.checkAvailability(request.getQuantity())) {
                return true;
            }
        }
        return false;
    }
}
