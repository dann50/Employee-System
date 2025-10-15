package com.dann50.apigateway.exception.handler;

import com.dann50.apigateway.exception.dto.ApiErrorDto;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> illegalArgument(IllegalArgumentException ex) {
        ApiErrorDto dto = new ApiErrorDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiErrorDto> jwtException(JwtException ex) {
        log.error(ex.getMessage(), ex);
        ApiErrorDto dto = new ApiErrorDto("Unable to parse JWT token", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }
}
