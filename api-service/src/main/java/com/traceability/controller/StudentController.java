package com.traceability.controller;
import com.traceability.dto.StudentDtoPost;
//import com.traceability.service.StudentService;
import com.traceability.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student")
@AllArgsConstructor
public class StudentController {
    private StudentService studentService;

    @PostMapping()
    public ResponseEntity<StudentDtoPost> createStudent(@RequestBody StudentDtoPost studentDto) {
        try {
            StudentDtoPost studentCreated = studentService.addStudent(studentDto);
            return new ResponseEntity<>(studentCreated, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(studentDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
