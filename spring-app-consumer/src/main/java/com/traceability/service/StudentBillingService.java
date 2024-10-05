package com.traceability.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traceability.entities.Student;
import com.traceability.repository.StudentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.traceability.configuration.KafkaProducerBillingConfig.TOPIC_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class StudentBillingService {
    private StudentRepository studentRepository;

    private KafkaTemplate<String, Student> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Used for JSON deserialization


    public void billStudent(String message) throws JsonProcessingException {
        Student student = objectMapper.readValue(message, Student.class);
        student.setFees(Math.random());
        studentRepository.save(student);
        kafkaTemplate.send(TOPIC_NAME, student);
        log.info("student fees compute {} and added to {} ", student.getFees(), student.getIdStudent());
        this.generateError();
    }

    private void generateError() {
        try {
            Object obj = null;
            obj.toString();
        } catch (Exception e) {
            log.error("this is an error log for demo");
            throw new RuntimeException(e);
        }

    }
}
