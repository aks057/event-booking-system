package com.cactro.eventbooking.dto.response;

import com.cactro.eventbooking.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private String eventVenue;
    private LocalDateTime eventDateTime;
    private Integer quantity;
    private Double totalPrice;
    private String customerName;
    private LocalDateTime bookedAt;

    public static BookingResponse from(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .eventId(booking.getEvent().getId())
                .eventTitle(booking.getEvent().getTitle())
                .eventVenue(booking.getEvent().getVenue())
                .eventDateTime(booking.getEvent().getDateTime())
                .quantity(booking.getQuantity())
                .totalPrice(booking.getTotalPrice())
                .customerName(booking.getCustomer().getName())
                .bookedAt(booking.getBookedAt())
                .build();
    }
}
