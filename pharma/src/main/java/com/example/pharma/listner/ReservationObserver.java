package com.example.pharma.listner;

import com.example.pharma.dto.events.ProductAvailableEvent;
import com.example.pharma.model.entity.P2P.ProductReservation;
import com.example.pharma.service.interfaces.IProductReservationService;
import com.example.pharma.service.interfaces.INotificationService;
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
    private final IProductReservationService reservationService;
    private final INotificationService notificationService;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Override
    public void onProductAvailable(ProductAvailableEvent event){
        var waitingReservations = reservationService.getPendingReservationsForProduct(event.productId());
        //notification logic
        for(ProductReservation res : waitingReservations)
        {
            log.info("Notifying user {} about availability of product {}", res.getUserId(), event.productName());
            if (res.getUser() != null && res.getUser().getUser() != null) {
                String email = res.getUser().getUser().getEmail();
                String subject = "Product Available: " + event.productName();
                String body = String.format("Hello %s,\n\nThe product '%s' is now available from %s (%s).\n\nBest regards,\nPharma Team",
                        res.getUser().getUser().getName(), event.productName(), event.producerName(), event.source());
                notificationService.sendEmailNotification(email, body, subject);
            }
        }
        
        if (!waitingReservations.isEmpty()) {
            reservationService.markReservationsAsNotified(waitingReservations);
        }
    }
}
