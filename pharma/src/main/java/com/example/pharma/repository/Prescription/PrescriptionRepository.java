package com.example.pharma.repository.Prescription;

import com.example.pharma.model.entity.prescription.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByCustomer_UserId(Long userId);
}