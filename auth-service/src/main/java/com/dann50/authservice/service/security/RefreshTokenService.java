package com.dann50.authservice.service.security;

import com.dann50.authservice.entity.RefreshToken;
import com.dann50.authservice.entity.User;
import com.dann50.authservice.exception.TokenRefreshException;
import com.dann50.authservice.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final TokenRepository tokenRepository;

    public RefreshTokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));
        return tokenRepository.save(refreshToken);
    }

    /**
     * If the token is valid, return it. If not, delete it and ask the
     * user to sign in.
     * @param token the token
     * @return the RefreshToken object
     */
    public RefreshToken verifyExpiration(String token) {
        var optionalToken = tokenRepository.findByToken(token);
        if (optionalToken.isPresent()) {
            var t = optionalToken.get();
            if (t.getExpiryDate().isAfter(Instant.now())) {
                return t;
            }
            tokenRepository.delete(t);
        }

        throw new TokenRefreshException("Token is invalid. Please sign in again");
    }

    public void deleteAllTokenByUser(User user) {
        tokenRepository.deleteAllByUser(user);
    }
}
