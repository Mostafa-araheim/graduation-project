package com.example.pharma.service;

import com.example.pharma.dto.Medicine.MedicineFilter;
import com.example.pharma.exception.validation.ValidationException;
import com.example.pharma.model.entity.catalog.Medicine;
import com.example.pharma.repository.Catalog.MedicineRepository;
import com.example.pharma.specification.MedicineSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicineRepository medicineRepository;
    private final LocationService locationService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("price", "name", "stockQuantity", "category");

        public List<Medicine> getMedicines(MedicineFilter filter, Sort sort) {
            validateFilter(filter);
            validateSort(sort);

            Specification<Medicine> spec = MedicineSpecification.buildFromFilter(filter, locationService);

            return medicineRepository.findAll(spec, sort);
    }

    private void validateFilter(MedicineFilter filter) {
        if (filter.minPrice() != null && filter.maxPrice() != null
                && filter.minPrice() > filter.maxPrice()) {
            throw new ValidationException("Minimum price cannot be greater than maximum price");
        }
        if (filter.maxDistanceKm() != null && filter.maxDistanceKm() < 0) {
            throw new ValidationException("Distance cannot be negative");
        }
        if (filter.maxDistanceKm() != null &&
                (filter.userLatitude() != null ||filter.userLongitude() != null )) {
            throw new ValidationException("Latitude and longitude are required when filtering by distance");
        }
    }

    private void validateSort(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return;
        }
        List<String> invalidFields = sort.stream()
                .map(Sort.Order::getProperty)
                .filter(property -> !ALLOWED_SORT_FIELDS.contains(property))
                .toList();

        if (!invalidFields.isEmpty()) {
            throw new ValidationException(
                    "Invalid sort fields: " + invalidFields + ". Allowed fields: " + ALLOWED_SORT_FIELDS
            );
        }
    }
}