package com.example.pharma.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryDto(@JsonProperty("category_name") String categoryName,
                          @JsonProperty("image_url") String imageUrl,
                          @JsonProperty("item_count") Long itemCount) {
}
