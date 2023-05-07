package com.nhnacademy.todolist.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class ErrorMessage {

    private int statusCode;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    private String message;

    private String path;

    public ErrorMessage(int statusCode, String message) {
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

    public ErrorMessage(int statusCode, String message, String path) {
        this.statusCode=statusCode;
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.path = path;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
