package com.example.pharma.mapper;

import com.example.pharma.dto.P2P.ListingRequest;
import com.example.pharma.dto.P2P.ListingResponse;
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
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "seller", source = "seller")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "city", source = "request.city")
    P2PListing toEntity(ListingRequest request, Product product, CustomerProfile seller, String imageUrl);

    @Mapping(target = "sellerId", source = "seller.userId")
    @Mapping(target = "sellerName", source = "seller.user.name")
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "createdAt", source = "createdAt.value")
    @Mapping(target = "sellerPhoneNumber", source = "seller.user.phone")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "condition", source = "condition")
    @Mapping(target = "categoryName", source = "product.category.categoryName")
    ListingResponse toResponse(P2PListing entity);
}
