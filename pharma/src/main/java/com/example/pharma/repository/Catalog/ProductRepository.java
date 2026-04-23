package com.example.pharma.repository.Catalog;

import com.example.pharma.model.entity.catalog.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Query("SELECT m FROM Product m WHERE LOWER(m.name) LIKE LOWER(concat(:name, '%'))")
    List<Product> searchByName(@Param("name") String name, Pageable pageable);
}
