package com.example.pharma.job;

import com.example.pharma.service.interfaces.IP2PListingService;
import com.example.pharma.service.interfaces.IProductReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpirationScheduler {

    private final IP2PListingService p2PListingService;
    private final IProductReservationService productReservationService;
    
    // Runs every day at 12:00 AM (midnight)
    @Scheduled(cron = "0 0 0 * * ?")
    public void expireOldRecords() {
        log.info("Starting expiration job for P2P Listings and Reservations.");

        int expiredListings = p2PListingService.expireOldListings();
        log.info("Expired {} P2P listings that passed their expiry date.", expiredListings);

        int expiredReservations = productReservationService.expireOldReservations();
        log.info("Expired {} reservations older than configured threshold.", expiredReservations);
    }
}
