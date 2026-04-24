package com.example.pharma.model.entity.inventory;

import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(
        name = "pharmacy_product",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"inventory_pharmacy_id", "product_id"})
        }
)
public class PharmacyProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pharmacyProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_pharmacy_id", referencedColumnName = "pharmacy_id", nullable = false)
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private BigDecimal price;

    private Long quantity;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus;
}