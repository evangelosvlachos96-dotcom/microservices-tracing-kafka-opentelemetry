package com.traceability.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "student", groupId = "group_id")
    public void consume(String message) {
        System.out.println("message = " + message);
    }
}

