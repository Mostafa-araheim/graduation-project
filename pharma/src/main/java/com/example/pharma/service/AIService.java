package com.example.pharma.service;

import com.example.pharma.dto.Location.CoordinateDto;
import com.example.pharma.dto.ai.*;
import com.example.pharma.dto.cart.request.CartItemIdentifierRequest;
import com.example.pharma.dto.cart.response.CartResponse;
import com.example.pharma.exception.prescription.PrescriptionScanFailedException;
import com.example.pharma.exception.prescription.PrescriptionScanTimeoutException;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.inventory.PharmacyProduct;
import com.example.pharma.repository.Catalog.ProductRepository;
import com.example.pharma.repository.Inventory.PharmacyProductRepository;
import com.example.pharma.service.cart.CartService;
import com.example.pharma.service.interfaces.ILocationService;
import com.example.pharma.specification.ProductSpecification;
import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {
    @Qualifier("openAiWebClient")
    private final WebClient openAiWebClient;

    @Qualifier("geminiWebClient")
    private final WebClient geminiWebClient;

    @Value("${api.gemini.key}")
    private String apiKey;
    private final PharmacyProductRepository pharmacyProductRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final CartService cartService;
    private final ILocationService locationService;
    private static final String OPENAI_PRESCRIPTION_PROMPT = """
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

    private static final String GEMINI_PRESCRIPTION_PROMPT = """
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
                    Each medicine must be its own separate object in the "medicines" array, even if multiple medicines appear on the same line, are combined in the same prescribed item, or share the same dosage/frequency. NEVER combine multiple drug names into a single "drug_name" field (e.g. do NOT write "Belladonna Tincture and Amphogel" as one entry — split them into two separate medicine objects, one for "Belladonna Tincture" and one for "Amphogel", each with its own form, category, dosage, and frequency).
                    Valid form values: Tablet, Capsule, Syrup, Suspension, Injection, Cream, Ointment, Inhaler, Suppository, Patch, Drops.
                    The form field MUST be one of the valid form values listed above. Pick the closest match if the exact form is not listed (e.g. "Solution" or "Liquid" -> "Syrup" or "Suspension", "Gel" -> "Ointment", "Lozenge" -> "Tablet").
                    Valid category values: Pain Relief, Antibiotics, Vitamins & Supplements, Cardiovascular, Diabetes, Dermatology, Respiratory, Gastrointestinal, Mental Health, Eye & Ear Care.
                    The category field MUST be one of the valid category values listed above. Pick the closest match if the exact category is not listed.
                    If the image is not a prescription or is unreadable, return: {"success": false, "medicines": []}
            """;

    public List<CartResponse> scanPrescription(
            MultipartFile image,
            Double userLatitude,
            Double userLongitude,
            Long userId
    ) {
        // 1. استدعاء OpenAI Vision لاستخراج أسماء الأدوية من الصورة
        ScanResponseDto scanResult = callApis(image);
        validateScanResult(scanResult);

        List<String> medicineNames = scanResult.medicines()
                .stream()
                .map(PredictionDto::drugName)
                .filter(name -> name != null && !name.isBlank())
                .toList();

//        List<String> medicineNames= List.of("amoxicillin", "panadol");

        log.info("Extracted medicine names from prescription: {}", medicineNames);

        // 2. البحث عن Products في الداتابيز بناءً على أسماء الأدوية
        List<Product> products =
                productRepository.findAll(ProductSpecification.nameMatchesAny(medicineNames));

        if (products.isEmpty()) {
            log.warn("No matching products found in catalog for names: {}", medicineNames);
            return List.of();
        }

        List<Long> productIds = products.stream()
                .map(Product::getProductId)
                .toList();

        // 3. جيب كل PharmacyProduct المتاحة لهذه الأدوية
        List<PharmacyProduct> availablePharmacyProducts =
                pharmacyProductRepository.findAvailableByProductIds(productIds);

        if (availablePharmacyProducts.isEmpty()) {
            log.warn("No pharmacy products available for productIds: {}", productIds);
            return List.of();
        }

        // 4. لكل product، اختار PharmacyProduct من أقرب صيدلية للمستخدم (مسافة طريق حقيقية)
        Map<Long, List<PharmacyProduct>> byProductId = availablePharmacyProducts.stream()
                .collect(Collectors.groupingBy(pp -> pp.getProduct().getProductId()));

        List<PharmacyProduct> selectedItems = new ArrayList<>();
        for (Product product : products) {
            List<PharmacyProduct> candidates = byProductId.get(product.getProductId());
            if (candidates == null || candidates.isEmpty()) {
                log.warn("No available pharmacy product for productId={}", product.getProductId());
                continue;
            }
            PharmacyProduct nearest = findNearestByRoad(candidates, userLatitude, userLongitude);
            selectedItems.add(nearest);
        }

        // 5. أضف كل دواء مختار للكارت (CartService يدير إيجاد أو إنشاء الكارت المناسب)
        for (PharmacyProduct pp : selectedItems) {
            cartService.addItem(userId, new CartItemIdentifierRequest(pp.getPharmacyProductId()));
            log.info("Added pharmacyProductId={} to cart for userId={}", pp.getPharmacyProductId(), userId);
        }

        // 6. إرجاع الكارتات المحدّثة للمستخدم
        return cartService.getUserCarts(userId);
    }

    /**
     * تسكن الروشتة وترجع:
     * - الأدوية اللي اتعرف عليها من الروشتة (scanned_medicines)
     * - أقرب 10 صيدليات مرتبة بمسافة الطريق (nearby_pharmacies)
     */
    public PrescriptionScanResult scanPrescriptionNearby(
            MultipartFile image,
            Double userLatitude,
            Double userLongitude
    ) {
        // 1. استدعاء OpenAI Vision لاستخراج أسماء الأدوية
        ScanResponseDto scanResult = callApis(image);
        validateScanResult(scanResult);

        List<String> medicineNames = scanResult.medicines()
                .stream()
                .map(PredictionDto::drugName)
                .filter(name -> name != null && !name.isBlank())
                .toList();

        log.info("Extracted medicine names from prescription: {}", medicineNames);

        // 2. البحث عن Products في الداتابيز
        List<Product> products =
                productRepository.findAll(ProductSpecification.nameMatchesAny(medicineNames));

        // mapping الـ products لـ ScannedProductDto
        List<ScannedProductDto> scannedMedicines = products.stream()
                .map(p -> new ScannedProductDto(
                        p.getProductId(),
                        p.getName(),
                        p.getDescription(),
                        p.isRequiresPrescription(),
                        p.getDosageForm() != null ? p.getDosageForm().name() : null,
                        p.getStrength(),
                        p.getManufacturer(),
                        p.getImageUrl(),
                        p.getCategory() != null ? p.getCategory().getCategoryName() : null,
                        p.getBrand()    != null ? p.getBrand().getBrandName()       : null
                ))
                .toList();

        if (products.isEmpty()) {
            log.warn("No matching products found for names: {}", medicineNames);
            return new PrescriptionScanResult(scannedMedicines, List.of());
        }

        int totalMedicinesRequested = products.size();

        List<Long> productIds = products.stream()
                .map(Product::getProductId)
                .toList();

        // 3. جيب كل PharmacyProducts المتاحة لهذه الأدوية
        List<PharmacyProduct> availablePharmacyProducts =
                pharmacyProductRepository.findAvailableByProductIds(productIds);

        if (availablePharmacyProducts.isEmpty()) {
            log.warn("No pharmacy products available for productIds: {}", productIds);
            return new PrescriptionScanResult(scannedMedicines, List.of());
        }

        // 4. Group by صيدلية — كل صيدلية فيها الأدوية المتاحة عندها
        Map<Long, List<PharmacyProduct>> byPharmacyId = availablePharmacyProducts.stream()
                .collect(Collectors.groupingBy(pp -> pp.getInventory().getPharmacy().getPharmacyId()));

        // 5. بناء list من الصيدليات الفريدة مع coordinates بتاعتها
        //    (نحتاج ترتيب ثابت عشان نربط مع roadDistances بالـ index)
        List<PharmacyProduct> pharmacyRepresentatives = byPharmacyId.values().stream()
                .map(list -> list.get(0))
                .toList();

        List<CoordinateDto> pharmacyCoords = pharmacyRepresentatives.stream()
                .map(pp -> {
                    var pharmacy = pp.getInventory().getPharmacy();
                    double lat = pharmacy.getLatitude()  != null ? pharmacy.getLongitude()  : 0.0;
                    double lon = pharmacy.getLongitude() != null ? pharmacy.getLatitude() : 0.0;
                    return new CoordinateDto(lat, lon);
                })
                .toList();

        // 6. batch call واحد لمسافات الطريق لكل الصيدليات
        List<Double> roadDistances =
                locationService.getRoadDistances(userLatitude, userLongitude, pharmacyCoords);

        // 7. بناء الـ response لكل صيدلية
        List<NearbyPharmacyResponse> pharmacyResults = new ArrayList<>();
        for (int i = 0; i < pharmacyRepresentatives.size(); i++) {
            var pharmacy = pharmacyRepresentatives.get(i).getInventory().getPharmacy();
            Long pharmacyId = pharmacy.getPharmacyId();
            double distanceKm = roadDistances.get(i);

            List<PharmacyProduct> medicinesInPharmacy = byPharmacyId.get(pharmacyId);

            // بناء قائمة الأدوية المتاحة في الصيدلية دي
            List<PrescriptionMedicineOption> medicineOptions = medicinesInPharmacy.stream()
                    .map(pp -> new PrescriptionMedicineOption(
                            pp.getPharmacyProductId(),
                            pp.getProduct().getProductId(),
                            pp.getProduct().getName(),
                            pp.getProduct().getImageUrl(),
                            pp.getProduct().getDosageForm() != null
                                    ? pp.getProduct().getDosageForm().name() : null,
                            pp.getProduct().getStrength(),
                            pp.getPrice(),
                            pp.getQuantity()
                    ))
                    .toList();

            // إجمالي السعر
            BigDecimal totalPrice = medicinesInPharmacy.stream()
                    .map(PharmacyProduct::getPrice)
                    .filter(p -> p != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            pharmacyResults.add(new NearbyPharmacyResponse(
                    pharmacyId,
                    pharmacy.getName(),
                    pharmacy.getImageUrl(),
                    pharmacy.getLatitude(),
                    pharmacy.getLongitude(),
                    distanceKm,
                    pharmacy.getAverageRating(),
                    pharmacy.isOpen(),
                    medicineOptions,
                    medicineOptions.size(),
                    totalMedicinesRequested,
                    totalPrice
            ));
        }

        // 8. ترتيب بالمسافة الأقرب أولاً وأخذ أقرب 10 صيدليات
        List<NearbyPharmacyResponse> sortedPharmacies = pharmacyResults.stream()
                .sorted(Comparator.comparingDouble(NearbyPharmacyResponse::distanceKm))
                .limit(10)
                .toList();

        return new PrescriptionScanResult(scannedMedicines, sortedPharmacies);
    }

    /**
     * يختار PharmacyProduct من أقرب صيدلية للمستخدم بناءً على مسافة الطريق الفعلية.
     * يستخدم getRoadDistances لإرسال batch call واحد بدل call لكل صيدلية.
     */
    private PharmacyProduct findNearestByRoad(
            List<PharmacyProduct> candidates,
            Double userLat,
            Double userLon
    ) {
        // بناء list من الـ coordinates لكل الصيدليات المرشحة
        List<CoordinateDto> pharmacyCoords = candidates.stream()
                .map(pp -> {
                    var pharmacy = pp.getInventory().getPharmacy();
                    double lat = pharmacy.getLatitude()  != null ? pharmacy.getLatitude()  : 0.0;
                    double lon = pharmacy.getLongitude() != null ? pharmacy.getLongitude() : 0.0;
                    return new CoordinateDto(lat, lon);
                })
                .toList();

        // batch call واحد → قائمة مسافات بنفس ترتيب pharmacyCoords
        List<Double> roadDistances =
                locationService.getRoadDistances(userLat, userLon, pharmacyCoords);

        // إيجاد الـ index اللي عنده أقل مسافة
        int nearestIndex = 0;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < roadDistances.size(); i++) {
            if (roadDistances.get(i) < minDistance) {
                minDistance = roadDistances.get(i);
                nearestIndex = i;
            }
        }

        return candidates.get(nearestIndex);
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
                                                "text", OPENAI_PRESCRIPTION_PROMPT
                                        )
                                )
                        )
                )
        );

        try {
            String rawResponse = openAiWebClient.post()
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
    private ScanResponseDto callGeminiVision(MultipartFile image) {
        String base64Image = encodeImageToBase64(image);
        String mediaType = image.getContentType() != null ? image.getContentType() : "image/jpeg";

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", GEMINI_PRESCRIPTION_PROMPT),
                                        Map.of(
                                                "inline_data", Map.of(
                                                        "mime_type", mediaType,
                                                        "data", base64Image
                                                )
                                        )
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "maxOutputTokens", 10000
                )
        );

        try {
            String rawResponse = geminiWebClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/gemini-2.5-flash:generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new PrescriptionScanFailedException(
                                            "Gemini API error: " + body
                                    ))
                    )
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(120))
                    .block();

            com.fasterxml.jackson.databind.ObjectMapper fasterxmlMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode rootNode = fasterxmlMapper.readTree(rawResponse);

            // Extract content from candidates[0].content.parts[0].text
            String content = rootNode
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return objectMapper.readValue(content, ScanResponseDto.class);

        } catch (TimeoutException e) {
            throw new PrescriptionScanTimeoutException("Gemini Vision request timed out");
        } catch (Exception e) {
            throw new PrescriptionScanFailedException("Failed to parse Gemini response: " + e.getMessage());
        }
    }
    private ScanResponseDto callApis(MultipartFile image) {
        ScanResponseDto scanResult;

        try {
            scanResult = callOpenAiVision(image);
            if (isUsableResult(scanResult)) {
                return scanResult;
            }
            log.warn("OpenAI returned an unusable result, falling back to Gemini");
        } catch (Exception e) {
            log.warn("OpenAI Vision call failed ({}), falling back to Gemini", e.getMessage());
        }

        scanResult = callGeminiVision(image);
        validateScanResult(scanResult);
        return scanResult;
    }
    private boolean isUsableResult(ScanResponseDto scanResult) {
        return scanResult != null
                && scanResult.success()
                && scanResult.medicines() != null
                && !scanResult.medicines().isEmpty();
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

}