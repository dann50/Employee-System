package com.dann50.authservice.service;

import com.dann50.authservice.dto.request.LoginRequest;
import com.dann50.authservice.dto.request.TokenRefreshRequest;
import com.dann50.authservice.dto.response.AuthenticationResponse;
import com.dann50.authservice.entity.RefreshToken;
import com.dann50.authservice.entity.Role;
import com.dann50.authservice.entity.User;
import com.dann50.authservice.exception.UserLoginException;
import com.dann50.authservice.repository.RoleRepository;
import com.dann50.authservice.repository.UserRepository;
import com.dann50.authservice.service.security.JwtService;
import com.dann50.authservice.service.security.RefreshTokenService;
import com.dann50.authservice.service.security.SecurityUser;
import com.dann50.authservice.util.EmployeeCreatedEvent;
import com.dann50.authservice.util.RoleName;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Business logic for endpoints relating to authentication.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService tokenService;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    public AuthService(UserRepository userRepository, RefreshTokenService tokenService,
                       JwtService jwtService, AuthenticationManager authManager,
                       PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    /**
     * Register User. The event enters the application from Kafka, and the
     * email, (default) password and roles get assigned accordingly.
     */
    public void registerUser(EmployeeCreatedEvent event) {
        List<RoleName> roleNames = new ArrayList<>(event.getRoles().size());
        for (var role: event.getRoles()) {
            roleNames.add(RoleName.valueOf("ROLE_" + role.toUpperCase()));
        }

        registerUser(event, roleNames);
    }

    private void registerUser(EmployeeCreatedEvent event, List<RoleName> roles) {
        List<Role> rls = roleRepository.findAllByNameIn(roles);

        User user = new User();
        user.setEmail(event.getEmail());
        user.setPassword(passwordEncoder.encode(event.getDefaultPassword()));
        user.setActive(true);
        user.setRoles(rls);

        userRepository.save(user);
    }

    public AuthenticationResponse loginUser(LoginRequest loginDto) {
        var auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        var user = (SecurityUser) auth.getPrincipal();

        if (!user.getUser().isActive()) {
            throw new UserLoginException("Can't login. Something went wrong");
        }

        String accessToken = generateAccessToken(user.getUser());
        String refreshToken = generateRefreshToken(user.getUser());

        var response = new AuthenticationResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");

        return response;
    }

    public void logout() {
        SecurityUser s = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        tokenService.deleteAllTokenByUser(s.getUser());
    }

    public void renderInactive(String email) {
        var opUser = userRepository.findByEmail(email);
        if (opUser.isEmpty())
            return;

        User user = opUser.get();
        tokenService.deleteAllTokenByUser(user);
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Checks to see if the token is valid (i.e has not expired.)
     * If it is, the new access token is generated and returned.
     * If not, the user has to log in again.
     */
    public AuthenticationResponse validateRefreshToken(TokenRefreshRequest refreshDto) {
        RefreshToken token = tokenService.verifyExpiration(refreshDto.getRefreshToken());

        String accessToken = generateAccessToken(token.getUser());

        var response = new AuthenticationResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(token.getToken());
        response.setTokenType("Bearer");

        return response;
    }

    private String generateAccessToken(User user) {
        return jwtService.generateToken(null, new SecurityUser(user));
    }

    private String generateRefreshToken(User user) {
        return tokenService.createRefreshToken(user).getToken();
    }
}
