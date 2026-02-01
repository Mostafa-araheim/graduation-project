package com.example.pharma.repository.Pharmacy;

import com.example.pharma.model.entity.pharmacy.PharmacyOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyOwnerRepository extends JpaRepository<PharmacyOwner, Integer> { }

