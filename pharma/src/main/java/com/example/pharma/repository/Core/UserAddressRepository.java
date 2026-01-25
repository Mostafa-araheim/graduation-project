package com.example.pharma.repository.Core;

import com.example.pharma.model.core.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> { }

