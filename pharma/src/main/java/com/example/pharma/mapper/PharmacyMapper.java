package com.example.pharma.mapper;

import com.example.pharma.dto.pharmacy.CreatePharmacyRequest;
import com.example.pharma.dto.pharmacy.PharmacyDto;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import com.example.pharma.model.entity.pharmacy.PharmacyAddress;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PharmacyMapper {
    PharmacyMapper INSTANCE = Mappers.getMapper(PharmacyMapper.class);
    PharmacyDto toDto(Pharmacy pharmacy);

    Pharmacy toPharmacy(CreatePharmacyRequest createPharmacyRequest);
    PharmacyAddress toPharmacyAddress(CreatePharmacyRequest createPharmacyRequest);

}
