package com.example.pharma.service;

import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.pharmacy.CreatePharmacyRequest;
import com.example.pharma.dto.pharmacy.PharmacyDto;
import com.example.pharma.dto.pharmacy.PharmacyInfo;
import com.example.pharma.dto.review.ReviewDto;
import com.example.pharma.mapper.PharmacyMapper;
import com.example.pharma.model.entity.catalog.Category;
import com.example.pharma.model.entity.catalog.Medicine;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import com.example.pharma.model.entity.pharmacy.PharmacyAddress;
import com.example.pharma.repository.Catalog.CategoryRepository;
import com.example.pharma.repository.Inventory.InventoryRecordRepository;
import com.example.pharma.repository.Pharmacy.PharmacyAddressRepository;
import com.example.pharma.repository.Pharmacy.PharmacyRepository;
import com.example.pharma.repository.Pharmacy.PharmacySpecifications;
import com.example.pharma.repository.Review.PharmacyReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private final CategoryRepository categoryRepository;
    private final PharmacyAddressRepository pharmacyAddressRepository;
    private final PharmacyReviewRepository pharmacyReviewRepository;
    private final InventoryRecordRepository inventoryRecordRepository;
    private final LocationService locationService;
    public PageResponse<PharmacyDto> getPharmacies( String name,
                                                    Float minRating,
                                                    Boolean isOpen,
                                                    Double latitude,
                                                    Double longitude,
                                                    Double maxDistanceKm,
                                                    Pageable pageable)
    {
        Specification<Pharmacy> spec = Specification.where(PharmacySpecifications.hasName(name))
                .and(PharmacySpecifications.hasMinRating(minRating))
                .and(PharmacySpecifications.isOpenNow(isOpen))
                .and(PharmacySpecifications.withinDistance(
                        latitude,
                        longitude,
                        maxDistanceKm,
                        locationService
                ));
        Page<Pharmacy> pharmacies = pharmacyRepository.findAll(spec, pageable);
        Page<PharmacyDto> pharmacyDtos = pharmacies.map(PharmacyMapper.INSTANCE::toDto);
        return PageResponse.from(pharmacyDtos);
    }
    public PharmacyInfo getPharmacyInfo(Long pharmacyId)
    {

        List<Category> categories = categoryRepository.findAll();
        PharmacyAddress pharmacyAddress = pharmacyAddressRepository.findById(pharmacyId).orElseThrow();
        Pharmacy pharmacy = pharmacyAddress.getPharmacy();
        PharmacyDto pharmacyDto = PharmacyMapper.INSTANCE.toDto(pharmacy);
        List<ReviewDto> pharmacyReviewDtos = pharmacyReviewRepository.findReviewDtosByPharmacyId(pharmacyId);
        return new PharmacyInfo(categories, pharmacyAddress, pharmacyDto, pharmacyReviewDtos);
    }
    public PageResponse<Medicine> getPharmacyMedicinesUnderACategory(Long pharmacyId, Long categoryId, Pageable pageable)
    {
       Page<Medicine> medicines = inventoryRecordRepository.findMedicinesByPharmacyAndCategory(pharmacyId, categoryId, pageable);
       return PageResponse.from(medicines);
    }

    @Transactional
    public void createPharmacies(List<CreatePharmacyRequest> requests)
    {
        List<Pharmacy> pharmacies = requests.stream().map(request -> {

            Pharmacy pharmacy = PharmacyMapper.INSTANCE.toPharmacy(request);
            PharmacyAddress address = PharmacyMapper.INSTANCE.toPharmacyAddress(request);
            address.setPharmacy(pharmacy);
            pharmacy.setAddress(address);
            return pharmacy;

        }).toList();

        pharmacyRepository.saveAll(pharmacies);
    }

}
