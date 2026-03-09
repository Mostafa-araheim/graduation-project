package com.example.pharma.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReviewDto(@JsonProperty("name") String name,
                        @JsonProperty("rating") Long rating,
                        @JsonProperty("comment") String comment) {
}
