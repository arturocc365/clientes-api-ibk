package com.ibk.clientesapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClienteNotFoundException.class)
    public Mono<org.springframework.http.ResponseEntity<ApiError>> handleNotFound(ClienteNotFoundException ex) {
        return Mono.just(org.springframework.http.ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("9999", ex.getMessage(), OffsetDateTime.now())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<org.springframework.http.ResponseEntity<ApiError>> handleValidation(WebExchangeBindException ex) {
        String message = ex.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Mono.just(org.springframework.http.ResponseEntity.badRequest()
                .body(new ApiError("4000", message, OffsetDateTime.now())));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<org.springframework.http.ResponseEntity<ApiError>> handleInput(ServerWebInputException ex) {
        return Mono.just(org.springframework.http.ResponseEntity.badRequest()
                .body(new ApiError("4000", ex.getReason(), OffsetDateTime.now())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<org.springframework.http.ResponseEntity<ApiError>> handleGeneric(Exception ex) {
        return Mono.just(org.springframework.http.ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("5000", ex.getMessage(), OffsetDateTime.now())));
    }
}

