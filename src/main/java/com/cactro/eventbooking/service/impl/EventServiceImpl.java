package com.cactro.eventbooking.service.impl;

import com.cactro.eventbooking.constant.ErrorCode;
import com.cactro.eventbooking.dto.request.EventRequest;
import com.cactro.eventbooking.dto.response.EventResponse;
import com.cactro.eventbooking.entity.Event;
import com.cactro.eventbooking.entity.User;
import com.cactro.eventbooking.event.EventUpdatedEvent;
import com.cactro.eventbooking.exception.BadRequestException;
import com.cactro.eventbooking.exception.ResourceNotFoundException;
import com.cactro.eventbooking.exception.UnauthorizedException;
import com.cactro.eventbooking.repository.BookingRepository;
import com.cactro.eventbooking.repository.EventRepository;
import com.cactro.eventbooking.repository.UserRepository;
import com.cactro.eventbooking.service.IEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements IEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<EventResponse> getAllEvents(String search, LocalDateTime dateFrom, Pageable pageable) {
        log.info("[getAllEvents] Request to get all events, search: {}, dateFrom: {}", search, dateFrom);
        return eventRepository.findEventsWithFilters(search, dateFrom, pageable)
                .map(EventResponse::from);
    }

    @Override
    public EventResponse getEventById(Long id) {
        log.info("[getEventById] Request to get event, id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id, ErrorCode.EVENT_NOT_FOUND));
        return EventResponse.from(event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventResponse createEvent(Long organizerId, EventRequest request) {
        log.info("[createEvent] Request to create event, organizerId: {}, title: {}", organizerId, request.getTitle());

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException(organizerId, ErrorCode.USER_NOT_FOUND));

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dateTime(request.getDateTime())
                .venue(request.getVenue())
                .totalTickets(request.getTotalTickets())
                .availableTickets(request.getTotalTickets())
                .price(request.getPrice())
                .organizer(organizer)
                .build();

        event = eventRepository.save(event);
        log.info("[createEvent] Event created successfully, id: {}", event.getId());
        return EventResponse.from(event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventResponse updateEvent(Long organizerId, Long eventId, EventRequest request) {
        log.info("[updateEvent] Request to update event, organizerId: {}, eventId: {}", organizerId, eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(eventId, ErrorCode.EVENT_NOT_FOUND));

        if (!event.getOrganizer().getId().equals(organizerId)) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        int ticketDifference = request.getTotalTickets() - event.getTotalTickets();
        int newAvailable = event.getAvailableTickets() + ticketDifference;
        if (newAvailable < 0) {
            throw new BadRequestException(ErrorCode.INVALID_TICKET_REDUCTION);
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setDateTime(request.getDateTime());
        event.setVenue(request.getVenue());
        event.setTotalTickets(request.getTotalTickets());
        event.setAvailableTickets(newAvailable);
        event.setPrice(request.getPrice());

        event = eventRepository.save(event);
        log.info("[updateEvent] Event updated successfully, id: {}", event.getId());

        // Trigger background notification to booked customers
        List<String> customerEmails = bookingRepository.findCustomerEmailsByEventId(eventId);
        if (!customerEmails.isEmpty()) {
            eventPublisher.publishEvent(new EventUpdatedEvent(eventId, event.getTitle(), customerEmails));
            log.info("[updateEvent] Published event update notification for {} customers", customerEmails.size());
        }

        return EventResponse.from(event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEvent(Long organizerId, Long eventId) {
        log.info("[deleteEvent] Request to delete event, organizerId: {}, eventId: {}", organizerId, eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(eventId, ErrorCode.EVENT_NOT_FOUND));

        if (!event.getOrganizer().getId().equals(organizerId)) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        eventRepository.delete(event);
        log.info("[deleteEvent] Event deleted successfully, id: {}", eventId);
    }
}
