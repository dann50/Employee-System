package com.dann50.authservice.controller;

import com.dann50.authservice.dto.request.LoginRequest;
import com.dann50.authservice.dto.request.TokenRefreshRequest;
import com.dann50.authservice.dto.response.AuthenticationResponse;
import com.dann50.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication",
    description = "Endpoints that handle sign-up, login and token issuing/refreshing"
)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "The \"standard\" login route. User submits" +
        " email and password, and the receives the access & refresh tokens if successful")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.accepted().body(authService.loginUser(loginRequest));
    }

    @PostMapping("/logout")
    @Operation(summary = "Log out", description = "User needs to be authenticated to call this route")
    public ResponseEntity<HttpStatus> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Access token refresh",
        description = "When the JWT access token expires, the client visits this endpoint" +
            " and supplies the refresh token to obtain a new one. If refresh token is valid," +
            " a new JWT token is returned, else, the client would have to sign in again."
    )
    public ResponseEntity<AuthenticationResponse> refreshAccessToken(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.validateRefreshToken(request));
    }

}
