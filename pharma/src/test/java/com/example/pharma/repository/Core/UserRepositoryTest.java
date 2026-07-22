package com.example.pharma.repository.Core;

import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.core.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should return user when searching by email and role if the user possesses that role")
    void findByEmailAndRolesContaining_ShouldReturnUser_WhenUserHasRole() {
        User user = new User();
        user.setEmail("owner@pharma.com");
        user.setName("Dr. Ahmed");
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ROLE_OWNER);
        roles.add(UserRole.ROLE_CUSTOMER);
        user.setRoles(roles);

        userRepository.save(user);

        Optional<User> foundOwner = userRepository.findByEmailAndRolesContaining("owner@pharma.com", UserRole.ROLE_OWNER);
        Optional<User> foundCustomer = userRepository.findByEmailAndRolesContaining("owner@pharma.com", UserRole.ROLE_CUSTOMER);

        assertTrue(foundOwner.isPresent());
        assertEquals("owner@pharma.com", foundOwner.get().getEmail());
        assertTrue(foundCustomer.isPresent());
    }

    @Test
    @DisplayName("Should return Optional.empty when searching with a role the user lacks or a non-existent email")
    void findByEmailAndRolesContaining_ShouldReturnEmpty_WhenRoleOrEmailNotMatches() {
        User user = new User();
        user.setEmail("customer@pharma.com");
        user.setName("Sarah");
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ROLE_CUSTOMER);
        user.setRoles(roles);

        userRepository.save(user);

        Optional<User> wrongRole = userRepository.findByEmailAndRolesContaining("customer@pharma.com", UserRole.ROLE_OWNER);
        Optional<User> wrongEmail = userRepository.findByEmailAndRolesContaining("notfound@pharma.com", UserRole.ROLE_CUSTOMER);

        assertTrue(wrongRole.isEmpty(), "Should not find user if they do not possess the required role");
        assertTrue(wrongEmail.isEmpty(), "Should return empty if the email does not exist");
    }

    @Test
    @DisplayName("Should correctly verify email existence using existsByEmail")
    void existsByEmail_ShouldReturnTrueOrFalseCorrectly() {
        User user = new User();
        user.setEmail("exists@pharma.com");
        user.setName("Ali");
        user.setRoles(Set.of(UserRole.ROLE_CUSTOMER));
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("exists@pharma.com"));
        assertFalse(userRepository.existsByEmail("doesnotexist@pharma.com"));
    }
}