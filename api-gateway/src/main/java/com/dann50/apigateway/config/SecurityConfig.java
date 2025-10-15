package com.dann50.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final ReactiveAuthenticationManager jwtAuthManager;
    private final ServerAuthenticationConverter jwtConverter;

    public SecurityConfig(ReactiveAuthenticationManager jwtAuthManager,
                          ServerAuthenticationConverter jwtConverter) {
        this.jwtAuthManager = jwtAuthManager;
        this.jwtConverter = jwtConverter;
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter authFilter = new AuthenticationWebFilter(jwtAuthManager);
        authFilter.setServerAuthenticationConverter(jwtConverter);
        authFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        http
            .csrf(c -> c.disable())
            .authorizeExchange(exchangeSpec ->
                exchangeSpec
                    .pathMatchers("/api/v1/auth/**").permitAll()
                    .pathMatchers("/api/v1/employees/**").permitAll()
                    .pathMatchers("/api/v1/departments/**").permitAll()
                    .anyExchange().authenticated()
            )
            .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        final var source = new UrlBasedCorsConfigurationSource();
        final var config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:8888", "http://localhost:8080", "http://localhost:8081",
            "http://localhost:8082", "http://localhost:8083", "http://localhost:8222"));
        config.setAllowedHeaders(List.of(HttpHeaders.ORIGIN, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT,
            HttpHeaders.AUTHORIZATION));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
