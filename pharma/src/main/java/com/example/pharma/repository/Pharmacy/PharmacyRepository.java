package com.example.pharma.repository.Pharmacy;

import com.example.pharma.model.pharmacy.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Integer> { }
