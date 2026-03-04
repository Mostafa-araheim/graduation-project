package com.example.pharma.dto.Inventory;

import com.example.pharma.model.entity.inventory.AvailabilityStatus;
import lombok.Data;

@Data
public class InventoryRecordDto {
    //from cookie
    private Integer inventoryId;
    private Integer medicineId;
    private Integer quantity;
    private AvailabilityStatus availabilityStatus;

}
