package com.valueinvesting.ruleone.exceptions;

import java.time.Instant;

public class ErrorResponse {
    private int statusCode;
    private String message;
    private Instant timestamp;

    public ErrorResponse() {}
    public ErrorResponse(int statusCode, String message, Instant timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
