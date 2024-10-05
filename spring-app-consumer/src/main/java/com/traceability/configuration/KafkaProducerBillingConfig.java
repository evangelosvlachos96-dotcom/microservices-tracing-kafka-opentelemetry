package com.traceability.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaProducerBillingConfig {

    public static final String TOPIC_NAME = "student-billing";

    @Bean
    public NewTopic createBillingTopic() {
        return new NewTopic(TOPIC_NAME, 3, (short) 1);
    }
}