package it.uniroma3.idd.search_engine.controller;

import it.uniroma3.idd.search_engine.utils.QueryParsingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(QueryParsingException.class)
    public ResponseEntity<String> handleQueryParsingException(QueryParsingException ex) {
        String errorMessage = "{ \"error\": \"Bad Request\", \"message\": \"" + ex.getMessage() + "\" }";

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }
}


