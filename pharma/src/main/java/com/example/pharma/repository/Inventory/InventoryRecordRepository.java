package com.example.pharma.repository.Inventory;

import com.example.pharma.model.entity.inventory.InventoryRecord;
import com.example.pharma.model.entity.inventory.InventoryRecordId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRecordRepository
        extends JpaRepository<InventoryRecord, InventoryRecordId> {
}
