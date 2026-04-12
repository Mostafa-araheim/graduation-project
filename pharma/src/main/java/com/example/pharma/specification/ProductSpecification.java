package com.example.pharma.specification;
import com.example.pharma.dto.Product.ProductFilter;
import com.example.pharma.model.entity.catalog.Product;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> buildFromFilter(ProductFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
                var categoryFetch = root.fetch("category", JoinType.INNER);
                root.fetch("brand", JoinType.LEFT);
            var categoryJoin = (Join<Object, Object>) categoryFetch;

            if (filter.categoryName() != null && !filter.categoryName().isBlank()) {
                predicates.add(buildCategoryFilter(categoryJoin, cb, filter));
            }

            if (filter.productName() != null && !filter.productName().isBlank()) {
                predicates.add(buildProductNameFilter(root, cb, filter));
            }

            if (filter.dosageForm() != null) {
                predicates.add(buildDosageFormFilter(root, cb, filter));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate buildCategoryFilter(Join<?, ?> categoryJoin,
                                                 CriteriaBuilder cb,
                                                 ProductFilter filter) {
        return cb.equal(
                cb.lower(categoryJoin.get("categoryName")),
                filter.categoryName().toLowerCase()
        );
    }

    private static Predicate buildProductNameFilter(Root<Product> root,
                                                    CriteriaBuilder cb,
                                                    ProductFilter filter) {
        return cb.like(
                cb.lower(root.get("name")),
                "%" + filter.productName().toLowerCase() + "%"
        );
    }

    private static Predicate buildDosageFormFilter(Root<Product> root,
                                                   CriteriaBuilder cb,
                                                   ProductFilter filter) {
        return cb.equal(root.get("dosageForm"), filter.dosageForm());
    }

}
