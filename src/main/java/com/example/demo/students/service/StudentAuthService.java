package com.example.demo.students.service;

import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.security.JwtService;
import com.example.demo.students.dto.LoginRequest;
import com.example.demo.students.dto.LoginResponse;
import com.example.demo.students.dto.StudentResponse;
import com.example.demo.students.entity.Student;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentAuthService {

    private final StudentService studentService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public StudentAuthService(
        StudentService studentService,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.studentService = studentService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Student student = studentService.findByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), student.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtService.generateToken(
            student.getId(),
            student.getEmail()
        );

        return new LoginResponse(
            "Login successful",
            token,
            StudentResponse.fromEntity(student)
        );
    }
}
