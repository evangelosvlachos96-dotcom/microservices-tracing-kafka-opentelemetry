package com.traceability.service;

import com.traceability.dto.StudentDtoPost;
import com.traceability.entities.Student;
import com.traceability.repository.StudentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.traceability.configuration.KafkaTopicConfig.TOPIC_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class StudentService {
    private StudentRepository studentRepository;
    private ModelMapper modelMapper;

    private KafkaTemplate<String, StudentDtoPost> kafkaTemplate;

    public StudentDtoPost addStudent(StudentDtoPost studentDto){
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE);
        Student student = modelMapper.map(studentDto, Student.class);
        studentRepository.save(student);
        kafkaTemplate.send(TOPIC_NAME, studentDto);
        log.info("Student added");
        return studentDto;
    }
}
