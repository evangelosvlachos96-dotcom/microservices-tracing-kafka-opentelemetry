package com.traceability.controller;


import com.traceability.dto.StudentDtoPost;
import com.traceability.repository.StudentRepository;
import com.traceability.service.StudentService;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import com.traceability.entities.Student;

@RestController
@RequestMapping("/student")
@AllArgsConstructor
public class StudentController {
    private StudentService studentService;

    @PostMapping("/")
    public ResponseEntity<StudentDtoPost> createStudent(@RequestBody StudentDtoPost studentDto) {
        try {
            StudentDtoPost studentCreated = studentService.addStudent(studentDto);
            return new ResponseEntity<>(studentCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(studentDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
