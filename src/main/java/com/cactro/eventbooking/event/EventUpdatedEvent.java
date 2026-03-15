package com.cactro.eventbooking.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EventUpdatedEvent {
    private final Long eventId;
    private final String eventTitle;
    private final List<String> bookedCustomerEmails;
}
