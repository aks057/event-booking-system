package com.cactro.eventbooking.service;

import com.cactro.eventbooking.dto.request.EventRequest;
import com.cactro.eventbooking.dto.response.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IEventService {

    Page<EventResponse> getAllEvents(String search, LocalDateTime dateFrom, Pageable pageable);

    EventResponse getEventById(Long id);

    EventResponse createEvent(Long organizerId, EventRequest request);

    EventResponse updateEvent(Long organizerId, Long eventId, EventRequest request);

    void deleteEvent(Long organizerId, Long eventId);
}
