package com.example.pharma.repository.P2P;

import com.example.pharma.model.entity.P2P.ListingStatus;
import com.example.pharma.model.entity.P2P.P2PListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface P2PListingRepository extends JpaRepository<P2PListing, Long>, JpaSpecificationExecutor<P2PListing> {
    Page<P2PListing> findBySeller_UserId(Long userId, Pageable pageable);
    
    boolean existsBySeller_UserIdAndProduct_ProductIdAndExpiryDateAndStatus(Long userId, Long productId, LocalDate expiryDate, ListingStatus status);
    
    long countBySeller_UserIdAndStatus(Long userId, ListingStatus status);

    @EntityGraph(attributePaths = {"product", "seller", "seller.user"})
    Optional<P2PListing> findById(Long id);

    @EntityGraph(attributePaths = {
            "product",
            "product.category",
            "seller",
            "seller.user"
    })
    Page<P2PListing> findAll(Pageable pageable);

    @Modifying
    @Query("UPDATE P2PListing p SET p.status = :expiredStatus WHERE p.status IN (:statuses) AND p.expiryDate < :currentDate")
    int expireOldListings(
            @Param("expiredStatus") ListingStatus expiredStatus,
            @Param("statuses") List<ListingStatus> statuses,
            @Param("currentDate") LocalDate currentDate);
}