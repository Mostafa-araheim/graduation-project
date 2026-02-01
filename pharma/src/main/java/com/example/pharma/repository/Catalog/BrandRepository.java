package com.example.pharma.repository.Catalog;

import com.example.pharma.model.entity.catalog.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> { }
