package com.dann50.authservice.service;

import com.dann50.authservice.util.EmployeeCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * The Kafka listener class. The methods annotated
 * with @KafkaListener will get triggered when a message is
 * consumed.
 */
@Slf4j
@Component
public class EmployeeEventConsumer {

    private final AuthService authService;

    public EmployeeEventConsumer(AuthService authService) {
        this.authService = authService;
    }

    @KafkaListener(
        topics = "employee.created", groupId = "auth-service-group",
        containerFactory = "empCreatedContainerFactory"
    )
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("Received employee created event with id: {}", event.getEmployeeId());
        authService.registerUser(event);
    }

    @KafkaListener(topics = "employee.deleted", groupId = "auth-service-group")
    public void handleEmployeeDeleted(String email) {
        log.info("Received employee deleted event: {}", email);
        authService.renderInactive(email);
    }
}
