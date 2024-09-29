package com.traceability.configuration;//package com.traceability.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String TOPIC_NAME = "student";

    @Bean
    public NewTopic createTopic() {
        return new NewTopic(TOPIC_NAME, 3, (short) 1);
    }
}