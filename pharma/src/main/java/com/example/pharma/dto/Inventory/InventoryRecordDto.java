package com.example.pharma.dto.Inventory;

import com.example.pharma.model.entity.inventory.AvailabilityStatus;
import lombok.Data;

@Data
public class InventoryRecordDto {
    //from cookie
    private Long inventoryId;
    private Long ProductId;
    private Long quantity;
    private AvailabilityStatus availabilityStatus;

}
