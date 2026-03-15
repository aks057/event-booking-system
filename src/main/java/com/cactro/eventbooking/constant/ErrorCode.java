package com.cactro.eventbooking.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Entity not found
    USER_NOT_FOUND("E001", "User not found"),
    EVENT_NOT_FOUND("E002", "Event not found"),
    BOOKING_NOT_FOUND("E003", "Booking not found"),

    // Validation errors
    EMAIL_ALREADY_EXISTS("E100", "Email already registered"),
    INVALID_CREDENTIALS("E101", "Invalid email or password"),
    INSUFFICIENT_TICKETS("E102", "Not enough tickets available"),
    INVALID_TICKET_REDUCTION("E103", "Cannot reduce total tickets below already booked count"),

    // Authorization errors
    UNAUTHORIZED_ACTION("E200", "User not authorized for this action"),
    INVALID_TOKEN("E201", "Invalid or expired token"),

    // Concurrency errors
    CONCURRENT_MODIFICATION("E300", "Concurrent modification detected. Please retry.");

    private final String code;
    private final String description;
}
