package com.example.pharma.repository.Pharmacy;

import com.example.pharma.model.entity.pharmacy.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Integer>, JpaSpecificationExecutor<Pharmacy> {
    List<Pharmacy> findByOwner_UserId(Integer ownerUserId);
}