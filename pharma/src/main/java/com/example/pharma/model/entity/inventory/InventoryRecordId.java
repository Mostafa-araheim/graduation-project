package com.example.pharma.model.entity.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRecordId implements Serializable {

    @Column(name = "inventory_id")
    private Integer inventoryId;

    @Column(name = "medicine_id")
    private Integer medicineId;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;
}