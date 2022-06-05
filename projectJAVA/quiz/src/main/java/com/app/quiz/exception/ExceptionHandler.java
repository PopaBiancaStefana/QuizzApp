package com.app.quiz.exception;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>("[Warning]Nu am gasit nimic.", HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException (NoSuchElementException e) {
        return new ResponseEntity<>("[Warning]Email-ul introdus nu exista.", HttpStatus.NOT_FOUND);
    }
}
