package com.cactro.eventbooking.exception;

import com.cactro.eventbooking.constant.ErrorCode;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final ErrorCode errorCode;

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public BadRequestException(String message) {
        super(message);
        this.errorCode = null;
    }
}
