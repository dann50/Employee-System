package com.dann50.employeeservice.service;

import com.dann50.employeeservice.util.EmployeeCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * The Kafka publisher class. The methods in this class
 * receive a message and publish it to the kafka broker
 * in the specified topics.
 */
@Service
public class EmployeeEventProducer {

    private final KafkaTemplate<String, EmployeeCreatedEvent> empCreatedTemplate;
    private final KafkaTemplate<String, String> empDeletedTemplate;

    @Value("${employee.topic.emp-created}")
    private String empCreatedTopic;

    @Value("${employee.topic.emp-deleted}")
    private String empDeletedTopic;

    public EmployeeEventProducer(KafkaTemplate<String, EmployeeCreatedEvent> empCreatedTemplate,
                                 KafkaTemplate<String, String> empDeletedTemplate) {
        this.empCreatedTemplate = empCreatedTemplate;
        this.empDeletedTemplate = empDeletedTemplate;
    }

    public void publishEmployeeCreatedEvent(EmployeeCreatedEvent event) {
        empCreatedTemplate.send(empCreatedTopic, event);
    }

    public void publishEmployeeDeletedEvent(String email) {
        empDeletedTemplate.send(empDeletedTopic, email);
    }
}
