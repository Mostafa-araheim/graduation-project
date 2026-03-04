package com.example.pharma.controller.medicine;

import com.example.pharma.dto.Medicine.MedicineFilter;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.model.entity.catalog.Medicine;
import com.example.pharma.service.MedicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicationService medicationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Medicine>>> getMedicines(
            @Valid @ModelAttribute MedicineFilter filter,
            Sort sort
    ) {
        List<Medicine> medicines = medicationService.getMedicines(filter, sort);

        return ResponseEntity.ok(
                ApiResponse.success("Medicines retrieved successfully", medicines)
        );
    }
}
