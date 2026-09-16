package com.example.demo.students.service;

import com.example.demo.exceptions.ConflictException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.students.dto.CreateStudentRequest;
import com.example.demo.students.dto.StudentResponse;
import com.example.demo.students.entity.Student;
import com.example.demo.students.mapper.StudentMapper;
import com.example.demo.students.repository.StudentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;

    public StudentService(
        StudentRepository studentRepository,
        StudentMapper studentMapper,
        PasswordEncoder passwordEncoder
    ) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public StudentResponse create(CreateStudentRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (studentRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("Email is already registered");
        }

        Student student = new Student();
        student.setName(request.name().trim());
        student.setEmail(normalizedEmail);
        student.setPassword(passwordEncoder.encode(request.password()));

        Student savedStudent = studentRepository.save(student);

        return studentMapper.toResponse(savedStudent);
    }

    @Transactional(readOnly = true)
    public Student findByEmail(String email) {
        return studentRepository.findByEmailIgnoreCase(normalizeEmail(email))
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
