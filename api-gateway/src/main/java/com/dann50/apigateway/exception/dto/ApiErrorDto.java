package com.dann50.apigateway.exception.dto;

public class ApiErrorDto {
    private final String message;
    private final int status;

    public ApiErrorDto(String message, int value) {
        this.message = message;
        this.status = value;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
