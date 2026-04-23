package com.example.pharma.listner;

import com.example.pharma.dto.events.ProductAvailableEvent;
import com.example.pharma.model.entity.P2P.ProductReservation;
import com.example.pharma.model.entity.P2P.ReservationStatus;
import com.example.pharma.repository.P2P.ProductReservationRepository;
import com.example.pharma.service.NotificationService;
import com.example.pharma.listner.interfaces.IReservationObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationObserver implements IReservationObserver {
    private final ProductReservationRepository reservationRepository;
    private final NotificationService notificationService;
    
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Override
    public void onProductAvailable(ProductAvailableEvent event){
        var waitingReservations = reservationRepository
                .findByProduct_ProductIdAndStatus(event.productId(), ReservationStatus.PENDING);
        //notification logic
        for(ProductReservation res : waitingReservations)
        {
            log.info("Notifying user {} about availability of product {}", res.getUserId(), event.productName());
            // Example of using notificationService: notificationService.sendNotification(res.getUserId(), "Product Available");
        }
    }
}
