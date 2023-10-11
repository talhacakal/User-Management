package com.jackal.user.management.utils;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SuccessResponse {

    private String message;
    private int httpStatus;
    private LocalDateTime time;

    public SuccessResponse(String message, int httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.time = LocalDateTime.now();
    }
}
