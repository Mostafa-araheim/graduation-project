package com.example.pharma.model.entity.inventory;

import com.example.pharma.model.entity.catalog.Medicine;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "inventory_record")
public class InventoryRecord {

    @EmbeddedId
    private InventoryRecordId id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("inventoryId")
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("medicineId")
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    private BigDecimal price;

    private Long quantity;

    @Column(name = "availability_status")
    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus;
}