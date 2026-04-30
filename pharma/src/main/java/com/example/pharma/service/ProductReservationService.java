package com.example.pharma.service;

import com.example.pharma.exception.resource.EntityAlreadyExistsException;
import com.example.pharma.model.entity.P2P.ProductReservation;
import com.example.pharma.model.entity.P2P.ReservationStatus;
import com.example.pharma.model.entity.catalog.Product;
import com.example.pharma.model.entity.core.CustomerProfile;
import com.example.pharma.repository.P2P.ProductReservationRepository;
import com.example.pharma.repository.Catalog.ProductRepository;
import com.example.pharma.repository.Core.CustomerProfileRepository;
import com.example.pharma.service.interfaces.IProductReservationService;
import com.example.pharma.dto.P2P.ReservationRequest;
import com.example.pharma.dto.P2P.ReservationResponse;
import com.example.pharma.mapper.ProductReservationMapper;
import com.example.pharma.exception.resource.EntityNotFoundException;
import com.example.pharma.dto.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReservationService implements IProductReservationService {

    private final ProductReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ProductReservationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductReservation> getPendingReservationsForProduct(Long productId) {
        return reservationRepository.findByProduct_ProductIdAndStatus(productId, ReservationStatus.PENDING);
    }

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        boolean alreadyExists = reservationRepository.existsByUserIdAndProduct_ProductIdAndStatusIn(
                request.userId(),
                request.productId(),
                List.of(ReservationStatus.PENDING)
        );

        if (alreadyExists) {
            throw new EntityAlreadyExistsException("You already have an active reservation for this product.");
        }
        CustomerProfile user = customerProfileRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.userId()));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + request.productId()));

        ProductReservation reservation = mapper.toEntity(user, product);

        ProductReservation saved = reservationRepository.save(reservation);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReservationResponse> getReservationsByUser(Long userId, Pageable pageable) {
        Page<ProductReservation> page = reservationRepository.findByUserId(userId, pageable);
        List<ReservationResponse> content = page.getContent().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    @Transactional
    public void markReservationsAsNotified(List<ProductReservation> reservations) {
        reservations.forEach(res -> res.setStatus(ReservationStatus.NOTIFIED));
        reservationRepository.saveAll(reservations);
    }

    @Override
    @Transactional
    public void deleteReservation(Long id) {
        ProductReservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + id));
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public int expireOldReservations() {
        java.time.LocalDateTime thresholdDate = java.time.LocalDateTime.now().minusDays(7);
        return reservationRepository.expireOldReservations(
                ReservationStatus.EXPIRED,
                List.of(ReservationStatus.PENDING, ReservationStatus.NOTIFIED),
                thresholdDate
        );
    }
}
