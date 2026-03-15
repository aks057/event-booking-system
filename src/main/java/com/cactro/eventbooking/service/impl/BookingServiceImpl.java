package com.cactro.eventbooking.service.impl;

import com.cactro.eventbooking.constant.ErrorCode;
import com.cactro.eventbooking.dto.request.BookingRequest;
import com.cactro.eventbooking.dto.response.BookingResponse;
import com.cactro.eventbooking.entity.Booking;
import com.cactro.eventbooking.entity.Event;
import com.cactro.eventbooking.entity.User;
import com.cactro.eventbooking.event.BookingConfirmedEvent;
import com.cactro.eventbooking.exception.BadRequestException;
import com.cactro.eventbooking.exception.ResourceNotFoundException;
import com.cactro.eventbooking.exception.UnauthorizedException;
import com.cactro.eventbooking.repository.BookingRepository;
import com.cactro.eventbooking.repository.EventRepository;
import com.cactro.eventbooking.repository.UserRepository;
import com.cactro.eventbooking.service.IBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements IBookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingResponse bookTickets(Long customerId, BookingRequest request) {
        log.info("[bookTickets] Request to book tickets, customerId: {}, eventId: {}, quantity: {}",
                customerId, request.getEventId(), request.getQuantity());

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(customerId, ErrorCode.USER_NOT_FOUND));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException(request.getEventId(), ErrorCode.EVENT_NOT_FOUND));

        if (event.getAvailableTickets() < request.getQuantity()) {
            throw new BadRequestException(ErrorCode.INSUFFICIENT_TICKETS);
        }

        // Decrement available tickets (optimistic locking via @Version will prevent overselling)
        event.setAvailableTickets(event.getAvailableTickets() - request.getQuantity());
        eventRepository.save(event);

        double totalPrice = event.getPrice() * request.getQuantity();

        Booking booking = Booking.builder()
                .customer(customer)
                .event(event)
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .build();

        booking = bookingRepository.save(booking);
        log.info("[bookTickets] Booking created successfully, id: {}", booking.getId());

        // Trigger background task for booking confirmation
        eventPublisher.publishEvent(new BookingConfirmedEvent(
                customer.getEmail(),
                customer.getName(),
                event.getTitle(),
                request.getQuantity(),
                totalPrice
        ));
        log.info("[bookTickets] Published booking confirmation event for customer: {}", customer.getEmail());

        return BookingResponse.from(booking);
    }

    @Override
    public List<BookingResponse> getMyBookings(Long customerId) {
        log.info("[getMyBookings] Request to get bookings, customerId: {}", customerId);
        return bookingRepository.findByCustomerId(customerId).stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Override
    public BookingResponse getBookingById(Long customerId, Long bookingId) {
        log.info("[getBookingById] Request to get booking, customerId: {}, bookingId: {}", customerId, bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(bookingId, ErrorCode.BOOKING_NOT_FOUND));

        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        return BookingResponse.from(booking);
    }
}
