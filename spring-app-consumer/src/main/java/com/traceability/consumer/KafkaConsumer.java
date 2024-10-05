package com.traceability.consumer;

import com.traceability.service.StudentBillingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KafkaConsumer {

    private final StudentBillingService studentBillingService;

    public KafkaConsumer(StudentBillingService studentBillingService) {
        this.studentBillingService = studentBillingService;
    }

    @KafkaListener(topics = "student", groupId = "group_id")
    public void consume(String message) throws IOException {
        studentBillingService.billStudent(message);

    }
}

