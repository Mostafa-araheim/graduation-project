package com.example.pharma.service;

import com.example.pharma.dto.ai.PredictionDto;
import com.example.pharma.dto.ai.ScanResponseDto;
import com.example.pharma.dto.pharmacyProduct.PharmacyProductFilter;
import com.example.pharma.exception.prescription.PrescriptionScanFailedException;
import com.example.pharma.exception.prescription.PrescriptionScanTimeoutException;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.repository.Catalog.ProductRepository;
import com.example.pharma.repository.Inventory.PharmacyProductRepository;
import com.example.pharma.service.interfaces.IPharmacyProductService;
import com.example.pharma.specification.ProductSpecification;
import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {
    private final WebClient aiWebClient;
    private final IPharmacyProductService pharmacyProductService;
    private final PharmacyProductRepository pharmacyProductRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private static final String PRESCRIPTION_PROMPT = """
            You are a medical prescription reader. Extract ALL medicines from this prescription image.
                    Return ONLY valid JSON with no explanation, markdown, or extra text. Use this exact format:
                    {
                      "success": true,
                      "medicines": [
                        {
                          "drug_name": "Amoxicillin",
                          "form": "Tablet",
                          "category": "Antibiotics",
                          "dosage": "500mg",
                          "frequency": "3x daily"
                        }
                      ]
                    }
                    Valid form values: Tablet, Capsule, Syrup, Injection, Cream, Drops, Ointment, Inhaler, Patch.
                    Valid category values: Pain Relief, Antibiotics, Vitamins & Supplements, Cardiovascular, Diabetes, Dermatology, Respiratory, Gastrointestinal, Mental Health, Eye & Ear Care.
                    The category field MUST be one of the valid category values listed above. Pick the closest match if the exact category is not listed.
                    If the image is not a prescription or is unreadable, return: {"success": false, "medicines": []}
            """;

    public List<Product> scanPrescription(
            MultipartFile image,
            Double userLatitude,
            Double userLongitude
    ) {
        //ScanResponseDto scanResult = callOpenAiVision(image);
        //validateScanResult(scanResult);
        //fetch the closest pharmacies with the medicines that they contain from the result
        List<String> medicineNames = List.of("Hair", "Activin", "Unizinc");
        List<Product> products =
                productRepository.findAll(ProductSpecification.nameMatchesAny(medicineNames));


        //List<PharmacyProduct> pharmacyProducts = pharmacyProductRepository.findAll()






        return products;
    }
    private ScanResponseDto callOpenAiVision(MultipartFile image) {
        String base64Image = encodeImageToBase64(image);
        String mediaType = image.getContentType() != null ? image.getContentType() : "image/jpeg";

        // Build the request body as a Map — no extra deps needed
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "max_tokens", 10000,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", List.of(
                                        Map.of(
                                                "type", "image_url",
                                                "image_url", Map.of(
                                                        "url", "data:" + mediaType + ";base64," + base64Image,
                                                        "detail", "high"
                                                )
                                        ),
                                        Map.of(
                                                "type", "text",
                                                "text", PRESCRIPTION_PROMPT
                                        )
                                )
                        )
                )
        );

        try {
            String rawResponse = aiWebClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new PrescriptionScanFailedException(
                                            "OpenAI API error: " + body
                                    ))
                    )
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(120))
                    .block();
            com.fasterxml.jackson.databind.ObjectMapper fasterxmlMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode rootNode = fasterxmlMapper.readTree(rawResponse);

            // Extract the content string from choices[0].message.content
            String content = rootNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            return objectMapper.readValue(content, ScanResponseDto.class);

        } catch (TimeoutException e) {
            throw new PrescriptionScanTimeoutException("OpenAI Vision request timed out");
        } catch (Exception e) {
            throw new PrescriptionScanFailedException("Failed to parse OpenAI response: " + e.getMessage());
        }
    }
    private String encodeImageToBase64(MultipartFile image) {
        try {
            return Base64.getEncoder().encodeToString(image.getBytes());
        } catch (IOException e) {
            throw new PrescriptionScanFailedException("Failed to read uploaded image");
        }
    }
    private void validateScanResult(ScanResponseDto scanResult) {
        if (scanResult == null) {
            throw new PrescriptionScanFailedException("No response received from OpenAI");
        }
        if (!scanResult.success()) {
            throw new PrescriptionScanFailedException("Could not process the prescription image");
        }
        if (scanResult.medicines() == null || scanResult.medicines().isEmpty()) {
            throw new PrescriptionScanFailedException("No medicines found in the prescription");
        }
    }

//    public PageResponse<pharmacyProductResponse> scanPrescription(
//            MultipartFile image,
//            Double userLatitude,
//            Double userLongitude
//    ) {
//        ScanResponseDto scanResult = callPythonApi(image);
//        validateScanResult(scanResult);
//
//        PharmacyProductFilter filter = buildFilter(scanResult.prediction(), userLatitude, userLongitude);
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("price").ascending());
//
//        return pharmacyProductService.getPharmacyProducts(filter, pageable);
//    }

//    private ScanResponseDto callPythonApi(MultipartFile image) {
//        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
//        bodyBuilder.part("file", image.getResource());
//
//        try {
//            return webClient.post()
//                    .uri("/predict")
//                    .contentType(MediaType.MULTIPART_FORM_DATA)
//                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
//                    .retrieve()
//                    .onStatus(
//                            status -> status.is4xxClientError() || status.is5xxServerError(),
//                            response -> response.bodyToMono(String.class)
//                                    .map(body -> new PrescriptionScanFailedException(
//                                            "OCR service returned an error: " + body
//                                    ))
//                    )
//                    .bodyToMono(ScanResponseDto.class)
//                    .timeout(Duration.ofSeconds(120))
//                    .block();
//
//        } catch (TimeoutException e) {
//            throw new PrescriptionScanTimeoutException("");
//        } catch (WebClientResponseException e) {
//            throw new PrescriptionScanFailedException("OCR service error: " + e.getMessage());
//        }
//    }

//    private void validateScanResult(ScanResponseDto scanResult) {
//        if (scanResult == null) {
//            throw new PrescriptionScanFailedException("No response received from OCR service");
//        }
//        if (!scanResult.success()) {
//            throw new PrescriptionScanFailedException("OCR service could not process the prescription");
//        }
//        if (scanResult.prediction() == null) {
//            throw new PrescriptionScanFailedException("Could not extract medicine details from prescription");
//        }
//        if (scanResult.prediction().drugName() == null || scanResult.prediction().drugName().isBlank()) {
//            throw new PrescriptionScanFailedException("Could not identify the medicine name from prescription");
//        }
//    }

    private PharmacyProductFilter buildFilter(PredictionDto prediction, Double lat, Double lng) {
        return new PharmacyProductFilter(
                null,              // productId
                prediction.drugName(),      // productName
                prediction.category(),      // categoryName
                prediction.form(),          // dosageForm
                lat,                        // userLatitude
                lng,                        // userLongitude
                10.0,                       // maxDistanceKm
                null,                       // minPrice
                null,                       // maxPrice
                true                        // inStock
        );
    }
}
