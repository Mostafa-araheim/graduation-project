package com.example.pharma.repository.P2P;

import com.example.pharma.model.entity.P2P.P2PListing;
import com.example.pharma.model.entity.P2P.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface P2PListingRepository extends JpaRepository<P2PListing, Long> {
    List<P2PListing> findBySeller_UserId(Long userId);
    
    boolean existsBySeller_UserIdAndProduct_ProductIdAndExpiryDateAndStatus(Long userId, Long productId, LocalDate expiryDate, ListingStatus status);
    
    long countBySeller_UserIdAndStatus(Long userId, ListingStatus status);
}