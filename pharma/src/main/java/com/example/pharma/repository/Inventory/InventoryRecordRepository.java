package com.example.pharma.repository.Inventory;

import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.inventory.Inventory;
import com.example.pharma.model.entity.inventory.InventoryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRecordRepository
        extends JpaRepository<InventoryRecord, Long> {

    List<InventoryRecord> findByInventory(Inventory inventory);

    @Query("""
        SELECT ir.product
        FROM InventoryRecord ir
        WHERE ir.inventory.pharmacy.pharmacyId = :pharmacyId
        AND ir.product.category.categoryId = :categoryId
    """)
    Page<Product> findProductsByPharmacyAndCategory(
            @Param("pharmacyId") Long pharmacyId,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}
