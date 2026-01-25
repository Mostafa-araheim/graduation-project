package com.example.pharma.repository.Pharmacy;

import com.example.pharma.model.pharmacy.PharmacyAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyAddressRepository extends JpaRepository<PharmacyAddress, Integer> { }
