package com.dann50.authservice.service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The logic for JWT token generation and signing. The signature
 * includes the email, roles and issue/expiry dates.
 */
@Service
public class JwtService {

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.secret-key}")
    private CharSequence secretKey;

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        if (extraClaims == null) { extraClaims = new HashMap<>(); }
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails,
                              long jwtExpiration) {
        List<String> l = new ArrayList<>();
        userDetails.getAuthorities().forEach(authority -> l.add(authority.getAuthority()));

        return Jwts
            .builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusMillis(jwtExpiration)))
            .claim("roles", l)
            .signWith(getSecretKey())
            .compact();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
