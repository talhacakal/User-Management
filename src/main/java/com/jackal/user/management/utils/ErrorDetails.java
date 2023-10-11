package com.jackal.user.management.utils;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorDetails {

    private Object error;
    private int code;
    private LocalDateTime timestamp;

    public ErrorDetails(Object error, int code) {
        this.error = error;
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }
}
