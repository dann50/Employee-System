package com.dann50.employeeservice.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationManager implements AuthenticationManager {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Override
    public Authentication authenticate(Authentication authentication) {
        if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
            return null;
        }

        String authToken = authentication.getCredentials().toString();

        try {
            Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .build()
                .parseSignedClaims(authToken)
                .getPayload();

            String username = claims.getSubject();
            Object roles = claims.get("roles");
            ArrayList<String> a = new ArrayList<>();
            if (roles instanceof List<?>) {
                for (Object role : (List<?>) roles) {
                    a.add(role.toString());
                }
            }

            List<GrantedAuthority> authorities = new ArrayList<>();
            a.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

            return new UsernamePasswordAuthenticationToken(username, null, authorities);

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage()); // Invalid token
        }
    }
}
