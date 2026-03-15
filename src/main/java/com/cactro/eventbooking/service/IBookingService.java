package com.cactro.eventbooking.service;

import com.cactro.eventbooking.dto.request.BookingRequest;
import com.cactro.eventbooking.dto.response.BookingResponse;

import java.util.List;

public interface IBookingService {

    BookingResponse bookTickets(Long customerId, BookingRequest request);

    List<BookingResponse> getMyBookings(Long customerId);

    BookingResponse getBookingById(Long customerId, Long bookingId);
}
