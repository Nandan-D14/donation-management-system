package com.donation.system.service;

import com.donation.system.model.entity.Donation;
import com.donation.system.model.entity.Inventory;
import com.donation.system.model.entity.Request;
import com.donation.system.repository.InventoryRepository;
import com.donation.system.repository.RequestRepository;
import com.donation.system.service.strategy.MatchingStrategy;
import com.donation.system.service.strategy.NormalMatchingStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * GRASP: Low Coupling
 *
 * InventoryService holds all inventory business logic and is the Strategy Pattern Context.
 * It depends only on the MatchingStrategy INTERFACE — not on any concrete strategy class.
 * This means strategies can be swapped at runtime without changing this service.
 *
 * InventoryService has NO direct dependency on Donor, Patient, or Admin modules —
 * it operates only through abstract types (Donation, Request) keeping coupling low.
 *
 * @author Neha (SRN 379)
 */
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final RequestRepository requestRepository;

    // Strategy Pattern Context: holds a reference to the current strategy (interface, not concrete)
    private MatchingStrategy matchingStrategy;

    /**
     * Constructor injection — GRASP Low Coupling requires no @Autowired field injection.
     * Defaults to NormalMatchingStrategy on startup.
     */
    public InventoryService(InventoryRepository inventoryRepository,
                            RequestRepository requestRepository,
                            NormalMatchingStrategy normalMatchingStrategy) {
        this.inventoryRepository = inventoryRepository;
        this.requestRepository = requestRepository;
        // Strategy Pattern: set default strategy at construction
        this.matchingStrategy = normalMatchingStrategy;
    }

    // ─── Strategy Pattern: Context Methods ────────────────────────────────────

    /**
     * Strategy Pattern applied here:
     * Swaps the active matching strategy at runtime — no restart needed.
     * Caller passes any MatchingStrategy implementation; service doesn't care which one.
     */
    public void setStrategy(MatchingStrategy strategy) {
        this.matchingStrategy = strategy;
    }

    /**
     * Delegates matching to the currently active strategy.
     * Strategy Pattern: the algorithm is determined by whichever strategy is set.
     */
    public List<Request> matchDonationToRequests(Donation donation) {

        if (matchingStrategy == null) {
            throw new IllegalStateException("Matching strategy not set");
        }

        List<Request> pending = requestRepository.findAll();
        // Strategy Pattern: delegates to current strategy implementation
        return matchingStrategy.match(donation, pending);
    }

    // ─── Stock Management ─────────────────────────────────────────────────────

    /**
     * Add stock for a blood type or organ type.
     * Creates a new Inventory record if one doesn't exist yet.
     */
    public Inventory addStock(String bloodType, String organType, int amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Optional<Inventory> existing =
                inventoryRepository.findByBloodTypeAndOrganType(bloodType, organType);

        Inventory inv = existing.orElseGet(() -> {
            Inventory newInv = new Inventory();
            newInv.setBloodType(bloodType);
            newInv.setOrganType(organType);
            newInv.setQuantity(0);
            return newInv;
        });

        inv.addStock(amount);
        return inventoryRepository.save(inv);
    }

    /**
     * Reduce stock for a blood/organ type.
     * Returns true if reduction succeeded, false if insufficient stock.
     */
    public boolean reduceStock(String bloodType, String organType, int amount) {
        Optional<Inventory> inv = inventoryRepository.findByBloodTypeAndOrganType(bloodType, organType);
        if (inv.isPresent() && inv.get().reduceStock(amount)) {
            inventoryRepository.save(inv.get());
            return true;
        }
        return false;
    }

    /**
     * Check whether sufficient stock exists for a blood/organ type.
     */
    public boolean checkAvailability(String bloodType, String organType, int amount) {
        Optional<Inventory> inv = inventoryRepository.findByBloodTypeAndOrganType(bloodType, organType);
        return inv.map(i -> i.checkAvailability(amount)).orElse(false);
    }

    /** Return all inventory records. */
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    /** Return inventory records with quantity below the given threshold (low stock alert). */
    public List<Inventory> getLowStock(int threshold) {
        return inventoryRepository.findByQuantityLessThan(threshold);
    }

    /** Find a specific inventory record by type. */
    public Optional<Inventory> findByType(String bloodType, String organType) {
        return inventoryRepository.findByBloodTypeAndOrganType(bloodType, organType);
    }
}
