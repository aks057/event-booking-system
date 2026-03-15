package com.cactro.eventbooking.controller;

import com.cactro.eventbooking.dto.request.EventRequest;
import com.cactro.eventbooking.dto.response.EventResponse;
import com.cactro.eventbooking.dto.response.Response;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/events")
public interface IEventController {

    @GetMapping
    ResponseEntity<Response<Page<EventResponse>>> getAllEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDateTime dateFrom,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    @GetMapping("/{id}")
    ResponseEntity<Response<EventResponse>> getEventById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<Response<EventResponse>> createEvent(
            Authentication authentication,
            @Valid @RequestBody EventRequest request);

    @PatchMapping("/{id}")
    ResponseEntity<Response<EventResponse>> updateEvent(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<Response<Void>> deleteEvent(
            Authentication authentication,
            @PathVariable Long id);
}
