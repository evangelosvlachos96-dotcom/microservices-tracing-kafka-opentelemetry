package com.traceability.service;

import com.traceability.dto.StudentDtoPost;
import com.traceability.entities.Student;
import com.traceability.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StudentService {
    private KafkaTemplate kafkaTemplate;
    private StudentRepository studentRepository;
    private ModelMapper modelMapper;
    public StudentDtoPost addStudent(StudentDtoPost studentDto){
            modelMapper.getConfiguration()
                    .setMatchingStrategy(MatchingStrategies.LOOSE);
            Student student = modelMapper.map(studentDto, Student.class);
            studentRepository.save(student);
            kafkaTemplate.send("topicStudent", student);
            return studentDto;
    }
}
