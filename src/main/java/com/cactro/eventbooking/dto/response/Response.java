package com.cactro.eventbooking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    @Builder.Default
    private long timestamp = Instant.now().toEpochMilli();

    private String status;
    private String message;
    private T data;
    private List<Error> errors;

    public static <T> Response<T> success(T data) {
        return Response.<T>builder()
                .status("OK")
                .data(data)
                .build();
    }

    public static <T> Response<T> success(String message, T data) {
        return Response.<T>builder()
                .status("OK")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> Response<T> error(String status, String message, List<Error> errors) {
        return Response.<T>builder()
                .status(status)
                .message(message)
                .errors(errors)
                .build();
    }
}
