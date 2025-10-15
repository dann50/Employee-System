package com.dann50.authservice.exception;

public class TokenRefreshException extends IllegalArgumentException {

    public TokenRefreshException(String message) {
        super(message);
    }
}
