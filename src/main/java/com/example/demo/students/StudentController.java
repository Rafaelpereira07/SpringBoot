package com.example.demo.students;

import com.example.demo.security.CurrentStudentProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Alunos")
public class StudentController {

    private final StudentService studentService;
    private final CurrentStudentProvider currentStudentProvider;

    public StudentController(StudentService studentService, CurrentStudentProvider currentStudentProvider) {
        this.studentService = studentService;
        this.currentStudentProvider = currentStudentProvider;
    }

    @PostMapping("/students/register")
    public ResponseEntity<StudentResponse> register(@Valid @RequestBody StudentRegisterRequest request) {
        StudentResponse response = studentService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Protected: requires a valid JWT. Returns the authenticated student's own account details. */
    @GetMapping("/account")
    public ResponseEntity<StudentResponse> account() {
        Long studentId = currentStudentProvider.requireStudentId();
        return ResponseEntity.ok(studentService.getById(studentId));
    }
}
