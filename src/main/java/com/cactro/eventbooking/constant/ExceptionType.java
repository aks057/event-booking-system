package com.cactro.eventbooking.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionType {

    BAD_REQUEST("BAD_REQUEST"),
    UNAUTHORIZED("UNAUTHORIZED"),
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    CONFLICT("CONFLICT");

    private final String type;

    public String get() {
        return type;
    }
}
