package com.datmai.moviereservation.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ExistingException extends RuntimeException {
    private final List<String> errors;
    public ExistingException(List<String> errors) {
        super("Validation failed");
        this.errors = errors;
    }
}
