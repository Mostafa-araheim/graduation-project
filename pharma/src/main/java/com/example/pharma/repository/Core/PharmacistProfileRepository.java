package com.example.pharma.repository.Core;

import com.example.pharma.model.entity.core.PharmacistProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PharmacistProfileRepository extends JpaRepository<PharmacistProfile, Long> {
    @EntityGraph(attributePaths = {"user"})
    Optional<PharmacistProfile> findByUserEmail(String email);
}
