package com.donation.system.repository;

import com.donation.system.model.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Inventory entity.
 * @author Neha (SRN 379)
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Optional<Inventory> findByBloodTypeAndOrganType(String bloodType, String organType);

    List<Inventory> findByQuantityLessThan(int quantity);
}