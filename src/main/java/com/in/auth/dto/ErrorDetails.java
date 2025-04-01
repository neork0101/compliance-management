package com.in.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ErrorDetails implements ResponseDto{
    @Schema(description = "HTTP status code", example = "400")
    private int statusCode;

    @Schema(description = "Error message", example = "Invalid credentials")
    private String message;

    @Schema(description = "Detailed error description", example = "Username or password is incorrect")
    private String details;

    public ErrorDetails(int statusCode, String message, String details) {
        this.statusCode = statusCode;
        this.message = message;
        this.details = details;
    }

    // Getters and setters
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
