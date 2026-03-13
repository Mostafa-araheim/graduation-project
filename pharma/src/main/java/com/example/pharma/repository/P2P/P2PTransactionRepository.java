package com.example.pharma.repository.P2P;

import com.example.pharma.model.entity.P2P.P2PTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface P2PTransactionRepository extends JpaRepository<P2PTransaction, Long> {
    Optional<P2PTransaction> findByListing_ListingId(Long listingId);
    List<P2PTransaction> findByBuyer_UserId(Long userId);
}