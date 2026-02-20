package com.example.pharma.repository.Inventory;

import com.example.pharma.model.entity.inventory.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByPharmacy_PharmacyId(Integer id);
}
