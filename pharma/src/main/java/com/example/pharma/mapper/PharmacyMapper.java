package com.example.pharma.mapper;

import com.example.pharma.dto.pharmacy.CreatePharmacyRequest;
import com.example.pharma.dto.pharmacy.PharmacyDto;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import com.example.pharma.model.entity.pharmacy.PharmacyAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PharmacyMapper {

    @Mapping(target = "distanceInKms", ignore = true)
    @Mapping(target = "isClosed", expression = "java(pharmacy.isClosed())")
    @Mapping(target = "address", expression = "java(mapAddress(pharmacy.getAddress()))")
    PharmacyDto toDto(Pharmacy pharmacy);
    List<PharmacyDto> toDtoList(List<Pharmacy> pharmacies);

    Pharmacy toPharmacy(CreatePharmacyRequest createPharmacyRequest);
    PharmacyAddress toPharmacyAddress(CreatePharmacyRequest createPharmacyRequest);
    default String mapAddress(PharmacyAddress address) {
        if (address == null) return null;

        String street = address.getStreet() != null ? address.getStreet() : "";
        String city = address.getCity() != null ? address.getCity() : "";

        if (street.isEmpty()) return city;
        if (city.isEmpty()) return street;

        return street + ", " + city;
    }
}
