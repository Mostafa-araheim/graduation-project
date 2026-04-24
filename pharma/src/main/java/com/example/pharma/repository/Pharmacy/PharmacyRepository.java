package com.example.pharma.repository.Pharmacy;

import com.example.pharma.model.entity.pharmacy.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long>, JpaSpecificationExecutor<Pharmacy> {
    List<Pharmacy> findByOwner_UserId(Long ownerUserId);
    Long countByOwner_UserId(Long ownerUserId);
    Page<Pharmacy> findByOwner_UserId(Long ownerUserId, Pageable pageable);

}