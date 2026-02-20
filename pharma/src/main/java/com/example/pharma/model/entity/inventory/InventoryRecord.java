package com.example.pharma.model.entity.inventory;

import com.example.pharma.model.entity.catalog.Medicine;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    private Integer quantity;

    @Column(name = "availability_status")
    private String availabilityStatus;
}