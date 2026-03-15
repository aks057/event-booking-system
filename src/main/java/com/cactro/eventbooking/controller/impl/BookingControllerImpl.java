package com.cactro.eventbooking.controller.impl;

import com.cactro.eventbooking.controller.IBookingController;
import com.cactro.eventbooking.dto.request.BookingRequest;
import com.cactro.eventbooking.dto.response.BookingResponse;
import com.cactro.eventbooking.dto.response.Response;
import com.cactro.eventbooking.service.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingControllerImpl implements IBookingController {

    private final IBookingService bookingService;

    @Override
    public ResponseEntity<Response<BookingResponse>> bookTickets(
            Authentication authentication, BookingRequest request) {
        Long customerId = (Long) authentication.getPrincipal();
        BookingResponse booking = bookingService.bookTickets(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Tickets booked successfully", booking));
    }

    @Override
    public ResponseEntity<Response<List<BookingResponse>>> getMyBookings(Authentication authentication) {
        Long customerId = (Long) authentication.getPrincipal();
        List<BookingResponse> bookings = bookingService.getMyBookings(customerId);
        return ResponseEntity.ok(Response.success(bookings));
    }

    @Override
    public ResponseEntity<Response<BookingResponse>> getBookingById(
            Authentication authentication, Long id) {
        Long customerId = (Long) authentication.getPrincipal();
        BookingResponse booking = bookingService.getBookingById(customerId, id);
        return ResponseEntity.ok(Response.success(booking));
    }
}
