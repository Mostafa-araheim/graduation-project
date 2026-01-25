package com.example.pharma.repository.Catalog;

import com.example.pharma.model.catalog.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Integer> { }
