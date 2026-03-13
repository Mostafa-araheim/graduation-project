package com.example.pharma.repository.Core;

import com.example.pharma.model.entity.core.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {}
