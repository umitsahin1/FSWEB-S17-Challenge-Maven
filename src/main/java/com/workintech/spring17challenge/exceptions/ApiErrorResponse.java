package com.workintech.spring17challenge.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private int status;
    private String message;
    private Long timestamp;

}
