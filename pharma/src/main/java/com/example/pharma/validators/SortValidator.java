package com.example.pharma.validators;

import com.example.pharma.exception.validation.ValidationException;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SortValidator {

    public static Sort validateAndMap(Sort sort, Map<String, String> allowedFieldsMapping) {
        if (sort == null || sort.isUnsorted()) {
            return sort;
        }

        List<String> invalidFields = sort.stream()
                .map(Sort.Order::getProperty)
                .filter(property -> !allowedFieldsMapping.containsKey(property))
                .toList();

        if (!invalidFields.isEmpty()) {
            throw new ValidationException(
                    "Invalid sort fields: " + invalidFields + ". Allowed fields: " + allowedFieldsMapping.keySet()
            );
        }

        List<Sort.Order> mappedOrders = sort.stream()
                .map(order -> new Sort.Order(
                        order.getDirection(),
                        allowedFieldsMapping.get(order.getProperty())
                ))
                .collect(Collectors.toList());

        return Sort.by(mappedOrders);
    }
}
