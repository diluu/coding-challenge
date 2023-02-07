package com.thudani.codingchallenge.util;

import org.springdoc.api.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<ErrorMessage> handleHttpException(ResponseStatusException ex) {
        ErrorMessage message = new ErrorMessage("Server Error: " + ex.getMessage());
        return new ResponseEntity<>(message, ex.getStatusCode());
    }
}