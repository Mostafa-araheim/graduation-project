package com.example.pharma.repository.Core;

import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.core.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndRolesContaining(String email, UserRole role);
}

