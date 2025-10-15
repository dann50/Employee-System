package com.dann50.employeeservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class TestConfig {

    @Bean
    public DBSetup dbSetup() {
        return new DBSetup();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("user123@email.com")
            .password("{noop}password")
            .roles("USER")
            .build();

        UserDetails user2 = User.builder()
            .username("admin123@email.com")
            .password("{noop}password")
            .roles("ADMIN")
            .build();

        UserDetails user3 = User.builder()
            .username("manager123@email.com")
            .password("{noop}password")
            .roles("MANAGER")
            .build();

        return new InMemoryUserDetailsManager(user, user2, user3);
    }
}
