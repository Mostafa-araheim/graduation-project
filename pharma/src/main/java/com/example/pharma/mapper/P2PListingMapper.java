package com.example.pharma.mapper;

import com.example.pharma.dto.P2P.ListingRequest;
import com.example.pharma.model.entity.P2P.P2PListing;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.core.CustomerProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface P2PListingMapper {
    
    @Mapping(target = "listingId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "seller", source = "seller")
    @Mapping(target = "product", source = "product")
    P2PListing toEntity(ListingRequest request, Product product, CustomerProfile seller);
}
