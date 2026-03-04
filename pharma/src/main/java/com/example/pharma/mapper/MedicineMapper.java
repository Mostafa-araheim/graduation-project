//package com.example.pharma.mapper;
//
//import com.example.pharma.dto.Medicine.MedicineResponse;
//import com.example.pharma.model.entity.catalog.Medicine;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(componentModel = "spring")
//public interface MedicineMapper {
//
//    @Mapping(source = "medicineId", target = "id")
//    @Mapping(source = "category.name", target = "category")
//    @Mapping(target = "image", ignore = true)
//    @Mapping(target = "price", ignore = true)
//    @Mapping(target = "originalPrice", ignore = true)
//    @Mapping(target = "inStock", ignore = true)
//    @Mapping(target = "pharmacyName", ignore = true)
//    @Mapping(target = "pharmacyDistance", ignore = true)
//    MedicineResponse toResponse(Medicine medicine);
//}

