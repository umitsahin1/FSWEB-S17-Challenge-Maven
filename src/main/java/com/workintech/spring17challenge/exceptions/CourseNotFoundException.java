package com.workintech.spring17challenge.exceptions;

import org.springframework.http.HttpStatus;

public class CourseNotFoundException extends ApiException {
    public CourseNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}