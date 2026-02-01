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

    @ManyToOne
    @MapsId("inventoryId")
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @ManyToOne
    @MapsId("medicineId")
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    private Integer quantity;
    private String availabilityStatus;
}
