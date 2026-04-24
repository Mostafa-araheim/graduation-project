package com.example.pharma.mapper.pharmacy;

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

    @Mapping(target = "pharmacyId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "averageRating", expression = "java(BigDecimal.ZERO)")
    @Mapping(target = "ratingCount", constant = "0L")
    @Mapping(target = "reviewCount", constant = "0L")
    @Mapping(target = "address", expression = "java(mapAddress(request))")
    Pharmacy toEntity(CreatePharmacyRequest request);

    default PharmacyAddress mapAddress(CreatePharmacyRequest request) {
        PharmacyAddress address = new PharmacyAddress();
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());
        address.setApartmentNumber(request.apartmentNumber());
        return address;
    }
}
