package com.example.pharma.validators;

import com.example.pharma.exception.resource.EntityAlreadyExistsException;
import com.example.pharma.exception.access.AccessDeniedException;
import com.example.pharma.model.entity.P2P.ProductReservation;
import com.example.pharma.model.entity.P2P.ReservationStatus;
import com.example.pharma.repository.P2P.ProductReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductReservationValidator {

    private final ProductReservationRepository reservationRepository;

    public void validateAlreadyExists(Long userId, Long productId) {
        boolean alreadyExists = reservationRepository.existsByUserIdAndProduct_ProductIdAndStatusIn(
                userId,
                productId,
                List.of(ReservationStatus.PENDING)
        );

        if (alreadyExists) {
            throw new EntityAlreadyExistsException("You already have an active reservation for this product.");
        }
    }

    public void validateReservationOwnership(Long userId, ProductReservation reservation) {
        if (!userId.equals(reservation.getCustomerProfile().getUserId())) {
            throw new AccessDeniedException("You do not have permission to modify or delete this reservation.");
        }
    }
}

