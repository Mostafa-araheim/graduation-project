package com.example.pharma.mapper;

import com.example.pharma.dto.Inventory.InventoryRecordDto;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.inventory.Inventory;
import com.example.pharma.model.entity.inventory.InventoryRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryRecordMapper {

    @Mapping(target = "inventory", source = "inventory")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "quantity", source = "dto.quantity")
    @Mapping(target = "availabilityStatus", source = "dto.availabilityStatus")
    InventoryRecord toEntity(
            InventoryRecordDto dto,
            Inventory inventory,
            Product product
    );
}

