package com.cactro.eventbooking.exception;

import com.cactro.eventbooking.constant.ErrorCode;
import com.cactro.eventbooking.constant.ExceptionType;
import com.cactro.eventbooking.dto.response.Error;
import com.cactro.eventbooking.dto.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response<Void>> handleNotFound(ResourceNotFoundException ex) {
        log.error("[handleNotFound] {}", ex.getMessage());
        List<Error> errors = new ArrayList<>();
        errors.add(Error.builder()
                .id(ex.getEntityId() != null ? ex.getEntityId().toString() : null)
                .type(ExceptionType.ENTITY_NOT_FOUND.get())
                .code(ex.getErrorCode() != null ? ex.getErrorCode().getCode() : null)
                .message(ex.getMessage())
                .build());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error("BAD_REQUEST", ex.getMessage(), errors));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Response<Void>> handleBadRequest(BadRequestException ex) {
        log.error("[handleBadRequest] {}", ex.getMessage());
        List<Error> errors = new ArrayList<>();
        errors.add(Error.builder()
                .type(ExceptionType.BAD_REQUEST.get())
                .code(ex.getErrorCode() != null ? ex.getErrorCode().getCode() : null)
                .message(ex.getMessage())
                .build());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error("BAD_REQUEST", ex.getMessage(), errors));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Response<Void>> handleUnauthorized(UnauthorizedException ex) {
        log.error("[handleUnauthorized] {}", ex.getMessage());
        List<Error> errors = new ArrayList<>();
        errors.add(Error.builder()
                .type(ExceptionType.UNAUTHORIZED.get())
                .code(ex.getErrorCode() != null ? ex.getErrorCode().getCode() : null)
                .message(ex.getMessage())
                .build());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.error("FORBIDDEN", ex.getMessage(), errors));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<Response<Void>> handleOptimisticLock(OptimisticLockingFailureException ex) {
        log.error("[handleOptimisticLock] Concurrent modification detected", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(Error.builder()
                .type(ExceptionType.CONFLICT.get())
                .code(ErrorCode.CONCURRENT_MODIFICATION.getCode())
                .message(ErrorCode.CONCURRENT_MODIFICATION.getDescription())
                .build());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Response.error("CONFLICT", ErrorCode.CONCURRENT_MODIFICATION.getDescription(), errors));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleValidation(MethodArgumentNotValidException ex) {
        log.error("[handleValidation] Validation failed");
        List<Error> errors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(Error.builder()
                    .type(ExceptionType.VALIDATION_ERROR.get())
                    .code(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error("BAD_REQUEST", "Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleGeneral(Exception ex) {
        log.error("[handleGeneral] Unexpected error", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(Error.builder()
                .type(ExceptionType.BAD_REQUEST.get())
                .message("An unexpected error occurred")
                .build());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred", errors));
    }
}
