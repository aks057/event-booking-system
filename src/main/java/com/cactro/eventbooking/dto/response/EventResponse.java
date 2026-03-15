package com.cactro.eventbooking.dto.response;

import com.cactro.eventbooking.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private String venue;
    private Integer totalTickets;
    private Integer availableTickets;
    private Double price;
    private String organizerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EventResponse from(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .dateTime(event.getDateTime())
                .venue(event.getVenue())
                .totalTickets(event.getTotalTickets())
                .availableTickets(event.getAvailableTickets())
                .price(event.getPrice())
                .organizerName(event.getOrganizer().getName())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
