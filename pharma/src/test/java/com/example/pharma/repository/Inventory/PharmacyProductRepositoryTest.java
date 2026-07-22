package com.example.pharma.repository.Inventory;

import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.core.OwnerProfile;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.model.entity.core.UserRole;
import com.example.pharma.model.entity.inventory.AvailabilityStatus;
import com.example.pharma.model.entity.inventory.Inventory;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PharmacyProductRepositoryTest {

    @Autowired
    private PharmacyProductRepository pharmacyProductRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Pharmacy savedPharmacy;
    private Product savedProduct;
    private OwnerProfile savedOwnerProfile;

    @BeforeEach
    void setUp() {
        // 1. Create and persist user and pharmacy owner
        User ownerUser = new User();
        ownerUser.setEmail("pharmacy_owner@test.com");
        ownerUser.setName("Dr. Mostafa");
        ownerUser.setRoles(Set.of(UserRole.ROLE_OWNER));
        ownerUser = entityManager.persistAndFlush(ownerUser);

        savedOwnerProfile = new OwnerProfile();
        savedOwnerProfile.setUser(ownerUser);
        savedOwnerProfile = entityManager.persistAndFlush(savedOwnerProfile);

        // 2. Create and persist the pharmacy and its inventory
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setName("El Amal Pharmacy");
        pharmacy.setOwner(savedOwnerProfile);
        savedPharmacy = entityManager.persistAndFlush(pharmacy);

        Inventory inventory = new Inventory();
        inventory.setPharmacy(savedPharmacy);
        entityManager.persistAndFlush(inventory);
        savedPharmacy.setInventory(inventory);

        // 3. Create and persist a product in the catalog
        Product product = new Product();
        product.setName("Amoxicillin 500mg");
        savedProduct = entityManager.persistAndFlush(product);
    }

    @Test
    @DisplayName("Should decrement stock successfully and return 1 when sufficient stock exists while status remains Available")
    void decrementStockIfEnough_ShouldReduceQuantityAndReturnOne_WhenStockIsSufficient() {
        // ─── 1. Arrange (Prepare stock of 10 items) ───
        PharmacyProduct pp = new PharmacyProduct();
        pp.setPharmacy(savedPharmacy);
        pp.setInventory(savedPharmacy.getInventory());
        pp.setProduct(savedProduct);
        pp.setPrice(new BigDecimal("120.00"));
        pp.setQuantity(10L);
        pp.setAvailabilityStatus(AvailabilityStatus.Available);
        entityManager.persistAndFlush(pp);

        // ─── 2. Act (Request decrement of 3 items) ───
        int updatedRows = pharmacyProductRepository.decrementStockIfEnough(
                savedPharmacy.getPharmacyId(),
                savedProduct.getProductId(),
                3
        );

        // Clear the EntityManager cache to ensure reading the new value directly from the database
        entityManager.clear();

        // ─── 3. Assert (Verify decrement and updated stock to 7) ───
        assertEquals(1, updatedRows, "The query should affect exactly one row");
        PharmacyProduct updated = pharmacyProductRepository.findById(pp.getPharmacyProductId()).orElseThrow();
        assertEquals(7L, updated.getQuantity(), "Remaining stock should be 10 - 3 = 7");
        assertEquals(AvailabilityStatus.Available, updated.getAvailabilityStatus());
    }

    @Test
    @DisplayName("Should automatically change product status to OutOfStock when full available stock is decremented (reaching zero)")
    void decrementStockIfEnough_ShouldSetOutOfStock_WhenRemainingQuantityIsZero() {
        // ─── 1. Arrange (Prepare stock of only 5 items) ───
        PharmacyProduct pp = new PharmacyProduct();
        pp.setPharmacy(savedPharmacy);
        pp.setInventory(savedPharmacy.getInventory());
        pp.setProduct(savedProduct);
        pp.setPrice(new BigDecimal("80.00"));
        pp.setQuantity(5L);
        pp.setAvailabilityStatus(AvailabilityStatus.Available);
        entityManager.persistAndFlush(pp);

        // ─── 2. Act (Request decrement of all 5 items) ───
        int updatedRows = pharmacyProductRepository.decrementStockIfEnough(
                savedPharmacy.getPharmacyId(),
                savedProduct.getProductId(),
                5
        );
        entityManager.clear();

        // ─── 3. Assert ───
        assertEquals(1, updatedRows);
        PharmacyProduct updated = pharmacyProductRepository.findById(pp.getPharmacyProductId()).orElseThrow();
        assertEquals(0L, updated.getQuantity(), "Remaining stock should be zero");
        assertEquals(AvailabilityStatus.OutOfStock, updated.getAvailabilityStatus(), "Status should automatically switch to OutOfStock");
    }

    @Test
    @DisplayName("Should not decrement stock and return zero when attempting to request a quantity larger than available stock")
    void decrementStockIfEnough_ShouldReturnZeroAndNotChangeStock_WhenStockIsInsufficient() {
        // ─── 1. Arrange (Prepare stock of only 2 items) ───
        PharmacyProduct pp = new PharmacyProduct();
        pp.setPharmacy(savedPharmacy);
        pp.setInventory(savedPharmacy.getInventory());
        pp.setProduct(savedProduct);
        pp.setPrice(new BigDecimal("150.00"));
        pp.setQuantity(2L);
        pp.setAvailabilityStatus(AvailabilityStatus.Available);
        entityManager.persistAndFlush(pp);

        // ─── 2. Act (Attempt to request 5 items) ───
        int updatedRows = pharmacyProductRepository.decrementStockIfEnough(
                savedPharmacy.getPharmacyId(),
                savedProduct.getProductId(),
                5
        );
        entityManager.clear();

        // ─── 3. Assert (No change in stock) ───
        assertEquals(0, updatedRows, "No row should be updated because stock is insufficient");
        PharmacyProduct notUpdated = pharmacyProductRepository.findById(pp.getPharmacyProductId()).orElseThrow();
        assertEquals(2L, notUpdated.getQuantity(), "Stock should remain unchanged");
        assertEquals(AvailabilityStatus.Available, notUpdated.getAvailabilityStatus());
    }

    @Test
    @DisplayName("Should accurately calculate the number of products owned by a specific pharmacist using countProductsByOwner")
    void countProductsByOwner_ShouldReturnCorrectCount() {
        // ─── 1. Arrange (Add a first product and a second product for the same pharmacy) ───
        PharmacyProduct pp1 = new PharmacyProduct();
        pp1.setPharmacy(savedPharmacy);
        pp1.setInventory(savedPharmacy.getInventory());
        pp1.setProduct(savedProduct);
        pp1.setQuantity(10L);
        entityManager.persistAndFlush(pp1);

        Product p2 = new Product();
        p2.setName("Panadol Extra");
        p2 = entityManager.persistAndFlush(p2);

        PharmacyProduct pp2 = new PharmacyProduct();
        pp2.setPharmacy(savedPharmacy);
        pp2.setInventory(savedPharmacy.getInventory());
        pp2.setProduct(p2);
        pp2.setQuantity(20L);
        entityManager.persistAndFlush(pp2);

        // ─── 2. Act ───
        Long count = pharmacyProductRepository.countProductsByOwner(savedOwnerProfile.getUserId());

        // ─── 3. Assert ───
        assertEquals(2L, count, "Pharmacist should have 2 products in their account");
    }
}