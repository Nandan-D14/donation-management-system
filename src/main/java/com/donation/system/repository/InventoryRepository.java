package com.donation.system.repository;

import com.donation.system.model.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByBloodType(String bloodType);
    Optional<Inventory> findByOrganType(String organType);
    Optional<Inventory> findByBloodTypeIgnoreCase(String bloodType);
    Optional<Inventory> findByOrganTypeIgnoreCase(String organType);
}
