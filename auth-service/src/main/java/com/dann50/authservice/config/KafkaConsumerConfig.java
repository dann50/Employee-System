package com.dann50.authservice.config;

import com.dann50.authservice.util.EmployeeCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * The Kafka consumer configuration. Sets up a factory for each
 * group of messages coming in. Uses a "strongly typed" setup to
 * ensure type safety, and if a message of a completely different
 * type is sent, it would need its own factory and listener.
 */
@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroup;

    @Bean
    public ConsumerFactory<String, EmployeeCreatedEvent> employeeCreatedFactory() {
        var jsonDeserializer = new JsonDeserializer<>(EmployeeCreatedEvent.class);
        jsonDeserializer.addTrustedPackages("com.dann50.authservice.util", "java.lang", "java.util");

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConsumerFactory<String, String> employeeDeletedFactory() {
        return new DefaultKafkaConsumerFactory<>(
            Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ConsumerConfig.GROUP_ID_CONFIG, consumerGroup,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
            )
        );
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(new FixedBackOff(0L, 0));

        // Optional: mark deserialization as non-retryable
        handler.addNotRetryableExceptions(DeserializationException.class);

        // Log the exception (Spring also logs by default)
        handler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.error("Error while processing record: {}", record);
            // ex.printStackTrace();
        });

        return handler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmployeeCreatedEvent> empCreatedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmployeeCreatedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(employeeCreatedFactory());
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> empDeletedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(employeeDeletedFactory());
        return factory;
    }
}
