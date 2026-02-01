package com.example.pharma.repository.P2P;

import com.example.pharma.model.entity.P2P.P2PTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface P2PTransactionRepository extends JpaRepository<P2PTransaction, Integer> { }
