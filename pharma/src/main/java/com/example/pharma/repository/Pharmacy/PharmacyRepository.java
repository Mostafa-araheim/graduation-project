package com.example.pharma.repository.Pharmacy;

import com.example.pharma.model.entity.pharmacy.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Integer> {
    List<Pharmacy> findByOwner_UserId(Integer ownerUserId);
}