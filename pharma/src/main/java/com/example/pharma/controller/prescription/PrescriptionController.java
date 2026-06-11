package com.example.pharma.controller.prescription;

import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/v1/scan-prescription")
@RestController
@RequiredArgsConstructor
public class PrescriptionController {

    private final AIService aiService;
      @PostMapping("/scan")
    public ResponseEntity<List<Product>> scanPrescription(
            @RequestParam("file") MultipartFile file,
            @RequestParam Double userLatitude,
            @RequestParam Double userLongitude
    ) {
        List<Product> result =
                aiService.scanPrescription(file, userLatitude, userLongitude);

        return ResponseEntity.ok(
                 result
        );
    }
}
