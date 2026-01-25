package com.example.pharma.model.inventory;

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

    private Integer inventoryId;
    private Integer medicineId;
    private LocalDate expiryDate;
}
