package com.example.pharma.controller.p2p;

import com.example.pharma.dto.P2P.ReservationRequest;
import com.example.pharma.dto.P2P.ReservationResponse;
import com.example.pharma.dto.common.PageResponse;
import com.example.pharma.service.interfaces.IProductReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/p2p/reservations")
@RequiredArgsConstructor
public class ProductReservationController {

    private final IProductReservationService reservationService;

    @GetMapping("/my-reservations")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PageResponse<ReservationResponse>> getReservationsByUser(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @PageableDefault(size = 10, sort = "reservationId") Pageable pageable) {
        PageResponse<ReservationResponse> response = reservationService.getReservationsByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        ReservationResponse response = reservationService.createReservation(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        reservationService.deleteReservation(userId, id);
        return ResponseEntity.noContent().build();
    }
}
