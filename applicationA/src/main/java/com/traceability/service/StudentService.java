package com.traceability.service;

import com.traceability.configuration.RabbitMQConfig;
import com.traceability.dto.StudentDtoPost;
import com.traceability.entities.Student;
import com.traceability.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StudentService {
    private StudentRepository studentRepository;
    private ModelMapper modelMapper;
    private RabbitTemplate rabbitTemplate;

    public StudentDtoPost addStudent(StudentDtoPost studentDto){
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE);
        Student student = modelMapper.map(studentDto, Student.class);
        studentRepository.save(student);
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, studentDto);
        return studentDto;
    }
}
