package com.example.pharma.dto.ai;

import java.util.List;

public record ScanResponseDto(
        boolean success,
        List<PredictionDto> medicines
) {}
