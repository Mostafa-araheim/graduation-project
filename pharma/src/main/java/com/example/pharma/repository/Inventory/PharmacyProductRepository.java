package com.example.pharma.repository.Inventory;

import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.inventory.Inventory;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyProductRepository
        extends JpaRepository<PharmacyProduct, Long> , JpaSpecificationExecutor<PharmacyProduct> {

    List<PharmacyProduct> findByInventory(Inventory inventory);

    @Query("""
        SELECT pp.product
        FROM PharmacyProduct pp
        WHERE pp.inventory.pharmacy.pharmacyId = :pharmacyId
        AND pp.product.category.categoryId = :categoryId
    """)
    Page<Product> findProductsByPharmacyAndCategory(
            @Param("pharmacyId") Long pharmacyId,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    Optional<PharmacyProduct> findByInventory_Pharmacy_PharmacyIdAndProduct_ProductId(
            Long pharmacyId, Long productId
    );
}
