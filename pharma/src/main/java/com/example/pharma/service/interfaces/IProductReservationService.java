package com.example.pharma.service.interfaces;

import com.example.pharma.model.entity.P2P.ProductReservation;
import com.example.pharma.dto.P2P.ReservationRequest;

import com.example.pharma.dto.P2P.ReservationResponse;
import com.example.pharma.dto.common.PageResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IProductReservationService {
    List<ProductReservation> getPendingReservationsForProduct(Long productId);

    PageResponse<ReservationResponse> getReservationsByUser(Long userId, Pageable pageable);

    ReservationResponse createReservation(ReservationRequest request);

    void markReservationsAsNotified(List<ProductReservation> reservations);

    void deleteReservation(Long id);

    int expireOldReservations();
}
