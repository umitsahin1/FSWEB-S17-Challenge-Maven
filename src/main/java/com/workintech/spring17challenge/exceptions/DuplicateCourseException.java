package com.workintech.spring17challenge.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateCourseException extends ApiException {
    public DuplicateCourseException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}