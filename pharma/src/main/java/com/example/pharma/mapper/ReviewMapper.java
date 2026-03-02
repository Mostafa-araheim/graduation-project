package com.example.pharma.mapper;

import com.example.pharma.dto.review.ReviewDto;
import com.example.pharma.model.entity.review.PharmacyReview;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);
    ReviewDto toDto(PharmacyReview pharmacyReview);
    List<ReviewDto> toDtoList(List<PharmacyReview> pharmacyReview);
}
