package com.example.pharma.mapper;

import com.example.pharma.dto.Inventory.InventoryRecordDto;
import com.example.pharma.model.entity.catalog.Medicine;
import com.example.pharma.model.entity.inventory.Inventory;
import com.example.pharma.model.entity.inventory.InventoryRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryRecordMapper {

    @Mapping(target = "id", expression = "java(new com.example.pharma.model.entity.inventory.InventoryRecordId(dto.getInventoryId(), dto.getMedicineId()))")
    @Mapping(target = "inventory", source = "inventory")
    @Mapping(target = "medicine", source = "medicine")
    @Mapping(target = "quantity", source = "dto.quantity")
    @Mapping(target = "availabilityStatus", source = "dto.availabilityStatus")
    InventoryRecord toEntity(InventoryRecordDto dto, Inventory inventory, Medicine medicine);
}

