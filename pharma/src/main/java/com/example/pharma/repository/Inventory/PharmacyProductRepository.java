package com.example.pharma.repository.Inventory;

import com.example.pharma.dto.pharmacyProduct.PharmacyProductDto;
import com.example.pharma.model.entity.inventory.AvailabilityStatus;
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
        extends JpaRepository<PharmacyProduct, Long>, JpaSpecificationExecutor<PharmacyProduct> {

    List<PharmacyProduct> findByInventory(Inventory inventory);

//    @Query("""
//        SELECT pp
//        FROM PharmacyProduct pp
//        WHERE pp.inventory.pharmacy.pharmacyId = :pharmacyId
//        AND pp.product.category.categoryId = :categoryId
//    """)
@Query("""
    SELECT new com.example.pharma.dto.pharmacyProduct.PharmacyProductDto(
        pp.pharmacyProductId,
        ph.name,
        p.productId,
        p.name,
        p.description,
        p.imageUrl,
        pp.price,
        pp.quantity,
        (pp.quantity > 0),
        p.requiresPrescription,
        p.dosageForm,
        p.strength,
        p.manufacturer,
        c.categoryId,
        c.categoryName,
        b.brandId,
        b.brandName
    )
    FROM PharmacyProduct pp
    JOIN pp.product p
    JOIN p.category c
    JOIN p.brand b
    JOIN pp.inventory i
    JOIN i.pharmacy ph
    WHERE ph.pharmacyId = :pharmacyId
    AND c.categoryId = :categoryId
""")
    Page<PharmacyProductDto> findProductsByPharmacyAndCategory(
            @Param("pharmacyId") Long pharmacyId,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );


    @Modifying
    @Query("""
        update PharmacyProduct pp
        set pp.quantity = pp.quantity - :requestedQty,
            pp.availabilityStatus = case
                when (pp.quantity - :requestedQty) > 0 
                then com.example.pharma.model.entity.inventory.AvailabilityStatus.Available
                else com.example.pharma.model.entity.inventory.AvailabilityStatus.OutOfStock
            end
        where pp.inventory.pharmacyId = :pharmacyId
          and pp.product.productId = :productId
          and pp.quantity >= :requestedQty
    """)
    int decrementStockIfEnough(
            @Param("pharmacyId") Long pharmacyId,
            @Param("productId") Long productId,
            @Param("requestedQty") Integer requestedQty
    );


    @Query("""
    select count(pp)
    from PharmacyProduct pp
    where pp.pharmacy.owner.userId = :ownerUserId
""")
    Long countProductsByOwner(@Param("ownerUserId") Long ownerUserId);


    @Query("""
    select count(pp)
    from PharmacyProduct pp
    where pp.pharmacy.owner.userId = :ownerUserId
      and pp.availabilityStatus = :status
""")
    Long countProductsByOwnerAndStatus(
            @Param("ownerUserId") Long ownerUserId,
            @Param("status") AvailabilityStatus status
    );


    Long countByInventory_PharmacyId(Long pharmacyId);

    Long countByInventory_PharmacyIdAndAvailabilityStatus(
            Long pharmacyId,
            AvailabilityStatus availabilityStatus
    );

    Page<PharmacyProduct> findByInventory(Inventory inventory, Pageable pageable);
    Optional<PharmacyProduct> findByInventory_PharmacyIdAndProduct_ProductId(Long pharmacyId, Long productId);


    @Query("""
    SELECT pp
    FROM PharmacyProduct pp
    JOIN FETCH pp.product p
    JOIN FETCH p.category c
    JOIN FETCH p.brand b
    JOIN FETCH pp.inventory i
    JOIN FETCH i.pharmacy ph
    WHERE pp.pharmacyProductId = :pharmacyProductId
""")
    Optional<PharmacyProduct> findWithDetailsById(
            @Param("pharmacyProductId") Long pharmacyProductId
    );

    @Query("""
        SELECT pp
        FROM PharmacyProduct pp
        JOIN FETCH pp.product p
        JOIN FETCH pp.inventory i
        JOIN FETCH i.pharmacy ph
        WHERE p.productId IN :productIds
          AND pp.quantity > 0
    """)
    List<PharmacyProduct> findAvailableByProductIds(
            @Param("productIds") List<Long> productIds
    );
}