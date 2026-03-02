package com.example.pharma.repository.Inventory;

import com.example.pharma.model.entity.catalog.Medicine;
import com.example.pharma.model.entity.inventory.Inventory;
import com.example.pharma.model.entity.inventory.InventoryRecord;
import com.example.pharma.model.entity.inventory.InventoryRecordId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRecordRepository
        extends JpaRepository<InventoryRecord, InventoryRecordId> {
    List<InventoryRecord> findByInventory(Inventory inventory);
    @Query("""
        SELECT ir.medicine
        FROM InventoryRecord ir
        WHERE ir.inventory.pharmacy.pharmacyId = :pharmacyId
        AND ir.medicine.category.categoryId = :categoryId
    """)
    Page<Medicine> findMedicinesByPharmacyAndCategory(
            @Param("pharmacyId") Integer pharmacyId,
            @Param("categoryId") Integer categoryId,
            Pageable pageable
    );
}
