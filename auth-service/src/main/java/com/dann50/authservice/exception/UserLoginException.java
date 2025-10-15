package com.dann50.authservice.exception;

public class UserLoginException extends IllegalArgumentException {

    public UserLoginException(String message) {
        super(message);
    }
}
