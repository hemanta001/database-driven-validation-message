package com.message.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class LocaleMessageException extends RuntimeException {
    private String message;
    private HttpStatus statusCode;
}
