package com.cactro.eventbooking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Date and time is required")
    @Future(message = "Event date must be in the future")
    private LocalDateTime dateTime;

    @NotBlank(message = "Venue is required")
    private String venue;

    @NotNull(message = "Total tickets is required")
    @Min(value = 1, message = "Total tickets must be at least 1")
    private Integer totalTickets;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;
}
