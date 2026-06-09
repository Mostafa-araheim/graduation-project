package com.example.pharma.repository.Core;

import com.example.pharma.model.entity.core.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUser_UserId(Long userId);
    Optional<UserAddress> findByUserAddressIdAndUser_UserId(Long userAddressId, Long userId);
}
