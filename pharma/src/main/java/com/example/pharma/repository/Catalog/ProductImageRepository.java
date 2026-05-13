package com.example.pharma.repository.Catalog;

import com.example.pharma.model.entity.catalog.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Query("""
        select pi
        from ProductImage pi
        join pi.product p
        join p.pharmacyProducts pp
        where pp.pharmacyProductId = :pharmacyProductId
          and pi.sortOrder = 1
    """)
    Optional<ProductImage> findPrimaryImageByPharmacyProductId(
            @Param("pharmacyProductId") Long pharmacyProductId
    );

    @Query("""
    select pi.imageUrl
    from ProductImage pi
    join pi.product p
    join p.pharmacyProducts pp
    where pp.pharmacyProductId = :pharmacyProductId
      and pi.sortOrder = 1
""")
    Optional<String> findPrimaryImageUrlByPharmacyProductId(
            @Param("pharmacyProductId") Long pharmacyProductId
    );
}
