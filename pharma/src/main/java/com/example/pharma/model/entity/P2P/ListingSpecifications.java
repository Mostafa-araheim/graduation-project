package com.example.pharma.model.entity.P2P;

import com.example.pharma.dto.P2P.ListingFilter;
import com.example.pharma.model.entity.catalog.Category;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.catalog.ProductCondition;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ListingSpecifications {
    private ListingSpecifications() {}

    public static Specification<P2PListing> hasCity(String city) {
        return (root, query, cb) ->
                city == null ? cb.conjunction() :
                        cb.like(
                                cb.lower(root.get("city")),
                                "%" + city.toLowerCase() + "%"
                        );
    }

    public static Specification<P2PListing> hasCondition(String condition) {
        return (root, query, cb) -> {
            if (condition == null) return cb.conjunction();
            try {
                ProductCondition c = ProductCondition.valueOf(condition.toUpperCase());
                return cb.equal(root.get("condition"), c);
            } catch (IllegalArgumentException e) {
                return cb.conjunction(); // invalid enum value, ignore filter
            }
        };
    }

    public static Specification<P2PListing> hasCategory(String categoryName) {
        return (root, query, cb) -> {
            if (categoryName == null) return cb.conjunction();

            Join<P2PListing, Product> product = root.join("product", JoinType.LEFT);
            Join<Product, Category> category = product.join("category", JoinType.LEFT);

            return cb.like(
                    cb.lower(category.get("categoryName")),
                    "%" + categoryName.toLowerCase() + "%"
            );
        };
    }
    public static Specification<P2PListing> hasProductName(String search) {
        return (root, query, cb) ->
                search == null ? cb.conjunction() :
                        cb.like(
                                cb.lower(root.get("productName")),
                                "%" + search.toLowerCase() + "%"
                        );
    }

    public static Specification<P2PListing> isAvailable() {
        return (root, query, cb) ->
                cb.equal(root.get("status"), ListingStatus.AVAILABLE);
    }

    public static Specification<P2PListing> build(ListingFilter filter) {
        return Specification
                .where(isAvailable())
                .and(hasCity(filter.city()))
                .and(hasCondition(filter.condition()))
                .and(hasCategory(filter.categoryName()))
                .and(hasProductName(filter.search()));
    }
}