package com.cactro.eventbooking.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookingConfirmedEvent {
    private final String customerEmail;
    private final String customerName;
    private final String eventTitle;
    private final int quantity;
    private final double totalPrice;
}
