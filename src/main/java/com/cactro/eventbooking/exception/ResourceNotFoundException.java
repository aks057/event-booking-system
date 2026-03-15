package com.cactro.eventbooking.exception;

import com.cactro.eventbooking.constant.ErrorCode;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final Long entityId;
    private final ErrorCode errorCode;

    public ResourceNotFoundException(Long entityId, ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.entityId = entityId;
        this.errorCode = errorCode;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.entityId = null;
        this.errorCode = null;
    }
}
