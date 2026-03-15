package com.cactro.eventbooking.controller.impl;

import com.cactro.eventbooking.controller.IEventController;
import com.cactro.eventbooking.dto.request.EventRequest;
import com.cactro.eventbooking.dto.response.EventResponse;
import com.cactro.eventbooking.dto.response.Response;
import com.cactro.eventbooking.service.IEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventControllerImpl implements IEventController {

    private final IEventService eventService;

    @Override
    public ResponseEntity<Response<Page<EventResponse>>> getAllEvents(
            String search, LocalDateTime dateFrom, int page, int size) {
        Page<EventResponse> events = eventService.getAllEvents(
                search, dateFrom, PageRequest.of(page, size, Sort.by("date_time").ascending()));
        return ResponseEntity.ok(Response.success(events));
    }

    @Override
    public ResponseEntity<Response<EventResponse>> getEventById(Long id) {
        EventResponse event = eventService.getEventById(id);
        return ResponseEntity.ok(Response.success(event));
    }

    @Override
    public ResponseEntity<Response<EventResponse>> createEvent(
            Authentication authentication, EventRequest request) {
        Long organizerId = (Long) authentication.getPrincipal();
        EventResponse event = eventService.createEvent(organizerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Event created successfully", event));
    }

    @Override
    public ResponseEntity<Response<EventResponse>> updateEvent(
            Authentication authentication, Long id, EventRequest request) {
        Long organizerId = (Long) authentication.getPrincipal();
        EventResponse event = eventService.updateEvent(organizerId, id, request);
        return ResponseEntity.ok(Response.success("Event updated successfully", event));
    }

    @Override
    public ResponseEntity<Response<Void>> deleteEvent(Authentication authentication, Long id) {
        Long organizerId = (Long) authentication.getPrincipal();
        eventService.deleteEvent(organizerId, id);
        return ResponseEntity.ok(Response.success("Event deleted successfully", null));
    }
}
