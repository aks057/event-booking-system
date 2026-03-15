package com.cactro.eventbooking.exception;

import com.cactro.eventbooking.constant.ErrorCode;
import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public UnauthorizedException(String message) {
        super(message);
        this.errorCode = null;
    }
}
