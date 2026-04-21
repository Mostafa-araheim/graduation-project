package com.example.pharma.service;

import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.dto.pharmacyProduct.PharmacyProductFilter;
import com.example.pharma.dto.pharmacyProduct.pharmacyProductResponse;
import com.example.pharma.mapper.PharmacyProductMapper;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import com.example.pharma.repository.Inventory.PharmacyProductRepository;
import com.example.pharma.specification.PharmacyProductSpecification;
import com.example.pharma.validators.PharmacyProductValidator;
import com.example.pharma.validators.SortValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import com.example.pharma.dto.Location.CoordinateDto;
import com.example.pharma.service.interfaces.IPharmacyProductService;
import com.example.pharma.service.interfaces.ILocationService;

@Service
@RequiredArgsConstructor
public class PharmacyProductService implements IPharmacyProductService {

    private final PharmacyProductRepository pharmacyProductRepository;
    private final ILocationService locationService;
    private final PharmacyProductMapper mapper;
    private final PharmacyProductValidator validator;

    // Fields to allow sorting on
    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "price", "price",
            "quantity", "quantity",
            "availabilityStatus", "availabilityStatus",
            "name", "product.name"
    );

    public PageResponse<pharmacyProductResponse> getPharmacyProducts(PharmacyProductFilter filter, Pageable pageable) {
        validator.validateFilter(filter);
        Sort mappedSort = SortValidator.validateAndMap(pageable.getSort(), SORT_FIELD_MAPPING);
        Pageable validatedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                mappedSort
        );
        Specification<PharmacyProduct> spec = PharmacyProductSpecification.buildFromFilter(filter, locationService);

        Page<PharmacyProduct> results = pharmacyProductRepository.findAll(spec, validatedPageable);
        List<pharmacyProductResponse> content = mapper.toResponseList(results.getContent());

        if (filter.userLatitude() != null && filter.userLongitude() != null) {
            content = calculateDistances(filter.userLatitude(), filter.userLongitude(), content);
        }
        var resultsPage= new PageImpl<>(
                content,
                validatedPageable,
                results.getTotalElements()
        );
        return PageResponse.from(resultsPage);
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
}
