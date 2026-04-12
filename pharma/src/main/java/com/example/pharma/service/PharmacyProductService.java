package com.example.pharma.service;

import com.example.pharma.dto.pharmacyProduct.PharmacyProductFilter;
import com.example.pharma.dto.pharmacyProduct.pharmacyProductResponse;
import com.example.pharma.exception.validation.ValidationException;
import com.example.pharma.mapper.PharmacyProductMapper;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import com.example.pharma.repository.Inventory.PharmacyProductRepository;
import com.example.pharma.specification.PharmacyProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import com.example.pharma.dto.Location.CoordinateDto;

@Service
@RequiredArgsConstructor
public class PharmacyProductService {

    private final PharmacyProductRepository pharmacyProductRepository;
    private final LocationService locationService;
    private final PharmacyProductMapper mapper;

    // Fields to allow sorting on
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "price",
            "quantity",
            "availabilityStatus",
            "product.name"
    );

    public List<pharmacyProductResponse> getPharmacyProducts(PharmacyProductFilter filter, Sort sort) {
        validateFilter(filter);
        validateSort(sort);

        Specification<PharmacyProduct> spec = PharmacyProductSpecification.buildFromFilter(filter, locationService);

        List<PharmacyProduct> results = pharmacyProductRepository.findAll(spec, sort);
        List<pharmacyProductResponse> responses = mapper.toResponseList(results);

        if (filter.userLatitude() != null && filter.userLongitude() != null) {
            responses = calculateDistances(filter.userLatitude(), filter.userLongitude(), responses);
        }

        return responses;
    }

    private List<pharmacyProductResponse> calculateDistances(
            double userLat, double userLon,
            List<pharmacyProductResponse> responses) {

        List<CoordinateDto> coords = responses.stream()
                .map(m -> new CoordinateDto(m.pharmacyLongitude(), m.pharmacyLatitude()))
                .toList();

        List<Double> distances = locationService.getRoadDistances(userLat, userLon, coords);

        return IntStream.range(0, responses.size())
                .mapToObj(i -> {
                    pharmacyProductResponse m = responses.get(i);
                    return new pharmacyProductResponse(
                            m.id(), m.pharmacyId(), m.productId(), m.productName(),
                            m.productImage(), m.price(), m.originalPrice(),
                            m.inStock(), m.category(), m.pharmacyName(),
                            m.pharmacyLatitude(), m.pharmacyLongitude(),
                            distances.get(i)
                    );
                })
                .toList();
    }

    private void validateFilter(PharmacyProductFilter filter) {
//        if ((filter.productName() == null || filter.productName().trim().isEmpty()) &&
//                (filter.categoryName() == null || filter.categoryName().trim().isEmpty())){
//            throw new ValidationException("at least one of product name and category names must be filled");
//        }

        if (filter.minPrice() != null && filter.maxPrice() != null
                && filter.minPrice() > filter.maxPrice()) {
            throw new ValidationException("Minimum price cannot be greater than maximum price");
        }
        if (filter.maxDistanceKm() != null && filter.maxDistanceKm() < 0) {
            throw new ValidationException("Distance cannot be negative");
        }
        if (filter.maxDistanceKm() != null &&
                (filter.userLatitude() == null || filter.userLongitude() == null)) {
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
