package com.example.pharma.service;

import com.example.pharma.dto.Product.ProductFilter;
import com.example.pharma.dto.Product.ProductResponse;
import com.example.pharma.exception.validation.ValidationException;
import com.example.pharma.mapper.ProductMapper;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.repository.Catalog.ProductRepository;
import com.example.pharma.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository ProductRepository;
    private final LocationService locationService;
    private final ProductMapper ProductMapper;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "category", "requiresPrescription", "dosageForm");

        public List<ProductResponse> getProducts(ProductFilter filter, Sort sort) {
            validateFilter(filter);
            validateSort(sort);

            Specification<Product> spec = ProductSpecification.buildFromFilter(filter);

            var Products = ProductRepository.findAll(spec, sort)
                    .stream()
                    .map(ProductMapper::toResponse)
                    .toList();
            return Products;
    }

//    public ProductResponse getProductById(Long id) {
//        validateProductId(id);
//
//        Product product = ProductRepository.findOne(ProductSpecification.byId(id))
//                .orElseThrow(() -> new ValidationException("Product with id " + id + " does not exist"));
//
//        return ProductMapper.toResponse(product);
//    }

//    private List<ProductResponse> calculateDistance(
//            double userLat, double userLon,
//            List<ProductResponse> Products) {
//
//        List<CoordinateDto> coords = Products.stream()
//                .map(m -> new CoordinateDto(m.pharmacyLongitude(), m.pharmacyLatitude()))
//                .toList();
//
//        List<Double> distances = locationService.getRoadDistances(userLat, userLon, coords);
//
//        return IntStream.range(0, Products.size())
//                .mapToObj(i -> {
//                    ProductResponse m = Products.get(i);
//                    return new ProductResponse(
//                            m.id(), m.name(), m.image(), m.price(), m.originalPrice(),
//                            m.inStock(), m.category(), m.pharmacyName(),
//                            m.pharmacyLatitude(), m.pharmacyLongitude(),
//                            distances.get(i)
//                    );
//                })
//                .toList();
//    }

//    private ProductResponse calculateDistance(
//            double userLat, double userLon,
//            ProductResponse Product) {
//
//        Double distance = locationService.getRoadDistance(
//                userLat, userLon,
//                new CoordinateDto(Product.pharmacyLatitude(), Product.pharmacyLongitude())
//        );
//
//        return new ProductResponse(
//                Product.id(), Product.name(), Product.image(),
//                Product.price(), Product.originalPrice(),
//                Product.inStock(), Product.category(), Product.pharmacyName(),
//                Product.pharmacyLatitude(), Product.pharmacyLongitude(),
//                distance
//        );
//    }

    private void validateFilter(ProductFilter filter) {
//        if (filter.minPrice() != null && filter.maxPrice() != null
//                && filter.minPrice() > filter.maxPrice()) {
//            throw new ValidationException("Minimum price cannot be greater than maximum price");
//        }
//        if (filter.maxDistanceKm() != null && filter.maxDistanceKm() < 0) {
//            throw new ValidationException("Distance cannot be negative");
//        }
//        if (filter.maxDistanceKm() != null &&
//                (filter.userLatitude() == null ||filter.userLongitude() == null )) {
//            throw new ValidationException("Latitude and longitude are required when filtering by distance");
//        }

        if (filter.productName() != null && filter.productName().trim().isEmpty()) {
            throw new ValidationException("Product name filter cannot be empty");
        }
        if (filter.categoryName() != null && filter.categoryName().trim().isEmpty()) {
            throw new ValidationException("Category name filter cannot be empty");
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

    private void validateProductId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Product id must be a positive number");
        }
        if (!ProductRepository.existsById(id)) {
            throw new ValidationException("Product with id " + id + " does not exist");
        }
    }
}