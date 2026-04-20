package com.example.pharma.repository.Inventory;

import com.example.pharma.model.entity.inventory.Inventory;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
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
        SELECT pp
        FROM PharmacyProduct pp
        WHERE pp.inventory.pharmacy.pharmacyId = :pharmacyId
        AND pp.product.category.categoryId = :categoryId
    """)
    Page<PharmacyProduct> findProductsByPharmacyAndCategory(
            @Param("pharmacyId") Long pharmacyId,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    Optional<PharmacyProduct> findByInventory_Pharmacy_PharmacyIdAndProduct_ProductId(
            Long pharmacyId, Long productId
    );

    @Modifying
    @Query("""
    update PharmacyProduct pp
    set pp.quantity = pp.quantity - :requestedQty,
        pp.availabilityStatus = case
            when (pp.quantity - :requestedQty) > 0 then com.example.pharma.model.entity.inventory.AvailabilityStatus.Available
            else com.example.pharma.model.entity.inventory.AvailabilityStatus.OutOfStock
        end
    where pp.inventory.pharmacy.pharmacyId = :pharmacyId
      and pp.product.productId = :productId
      and pp.quantity >= :requestedQty
""")
    int decrementStockIfEnough(
            @Param("pharmacyId") Long pharmacyId,
            @Param("productId") Long productId,
            @Param("requestedQty") Integer requestedQty
    );
}
