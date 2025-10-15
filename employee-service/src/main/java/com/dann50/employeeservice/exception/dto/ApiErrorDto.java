package com.dann50.employeeservice.exception.dto;

public class ApiErrorDto {

    private final String message;
    private final int code;

    public ApiErrorDto(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
