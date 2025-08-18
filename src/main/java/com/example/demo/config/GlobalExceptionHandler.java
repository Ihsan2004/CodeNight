package com.example.demo.config;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(IllegalArgumentException ex) {
        return ApiError.builder()
                .timestamp(Instant.now().toString())
                .status(400)
                .error("Bad Request")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGeneric(Exception ex) {
        return ApiError.builder()
                .timestamp(Instant.now().toString())
                .status(500)
                .error("Internal Server Error")
                .message(ex.getMessage())
                .build();
    }

    @Value
    @Builder
    static class ApiError {
        String timestamp;
        int status;
        String error;
        String message;
    }
}

