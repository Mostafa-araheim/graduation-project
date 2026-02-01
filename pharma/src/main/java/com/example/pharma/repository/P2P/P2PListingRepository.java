package com.example.pharma.repository.P2P;

import com.example.pharma.model.entity.P2P.P2PListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface P2PListingRepository extends JpaRepository<P2PListing, Integer> { }

