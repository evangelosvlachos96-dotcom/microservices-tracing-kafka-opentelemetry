package com.traceability.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traceability.entities.Student;
import com.traceability.repository.StudentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.traceability.configuration.KafkaProducerBillingConfig.TOPIC_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class StudentBillingService {
    private StudentRepository studentRepository;

    private KafkaTemplate<String, Student> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void billStudent(String message) throws JsonProcessingException {
        Student student = objectMapper.readValue(message, Student.class);
        student.setFees(Math.random());
        studentRepository.save(student);
        kafkaTemplate.send(TOPIC_NAME, student);
        log.info("student fees compute {} and added to {} ", student.getFees(), student.getIdStudent());
    }
}
