package com.traceability.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component

// Class
public class KafkaConsumer {

    @KafkaListener(topics = "topicStudent",
            groupId = "group_id")

    // Method
    public void
    consume(String message)
    {
        // Print statement
        System.out.println("message = " + message);
    }
}

