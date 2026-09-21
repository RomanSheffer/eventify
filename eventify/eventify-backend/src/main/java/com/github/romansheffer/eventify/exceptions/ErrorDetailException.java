package com.github.romansheffer.eventify.exceptions;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorDetailException extends RuntimeException{

    private final String field;
    private final HttpStatus status;

    public ErrorDetailException(String field, String message, HttpStatus status) {
        super(message);
        this.field = field;
        this.status = status;
    }

}
