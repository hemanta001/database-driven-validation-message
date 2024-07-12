package com.message.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Order(1)
public class LocaleMessageExceptionHandler {

    @ExceptionHandler(LocaleMessageException.class)
    protected ResponseEntity<Object> handleLocalApiException(LocaleMessageException exception) {
        return new ResponseEntity<>(Map.of("error", exception.getMessage()), exception.getStatusCode());
    }
}
