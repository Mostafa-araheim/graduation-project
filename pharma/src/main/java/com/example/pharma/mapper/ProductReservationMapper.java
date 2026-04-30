package com.example.pharma.mapper;

import com.example.pharma.dto.P2P.ReservationResponse;
import com.example.pharma.model.entity.P2P.ProductReservation;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.core.CustomerProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductReservationMapper {

    @Mapping(target = "createdAt", expression = "java(reservation.getCreatedAt() != null && reservation.getCreatedAt().getValue() != null ? reservation.getCreatedAt().getValue() : java.time.LocalDateTime.now())")
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "productId", source = "product.productId")
    ReservationResponse toResponse(ProductReservation reservation);

    @Mapping(target = "reservationId", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userId", ignore = true)     // read-only field
    @Mapping(target = "productId", ignore = true)  // read-only field
    ProductReservation toEntity(CustomerProfile user, Product product);
}
