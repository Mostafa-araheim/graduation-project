package com.example.pharma.mapper.cart;

import com.example.pharma.dto.cart.request.CreateCartRequest;
import com.example.pharma.model.cart.CartMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring", imports = {Instant.class})
public interface CartMapper {

    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    CartMetadata toEntity(CreateCartRequest request);
}
