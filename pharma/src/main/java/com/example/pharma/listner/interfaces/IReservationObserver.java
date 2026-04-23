package com.example.pharma.listner.interfaces;

import com.example.pharma.dto.events.ProductAvailableEvent;

public interface IReservationObserver {
    void onProductAvailable(ProductAvailableEvent event);
}
