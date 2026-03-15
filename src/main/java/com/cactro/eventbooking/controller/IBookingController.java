package com.cactro.eventbooking.controller;

import com.cactro.eventbooking.dto.request.BookingRequest;
import com.cactro.eventbooking.dto.response.BookingResponse;
import com.cactro.eventbooking.dto.response.Response;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/bookings")
public interface IBookingController {

    @PostMapping
    ResponseEntity<Response<BookingResponse>> bookTickets(
            Authentication authentication,
            @Valid @RequestBody BookingRequest request);

    @GetMapping
    ResponseEntity<Response<List<BookingResponse>>> getMyBookings(Authentication authentication);

    @GetMapping("/{id}")
    ResponseEntity<Response<BookingResponse>> getBookingById(
            Authentication authentication,
            @PathVariable Long id);
}
