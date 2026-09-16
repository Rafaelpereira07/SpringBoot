package com.example.demo.students.dto;

import com.example.demo.students.entity.Student;

import java.time.LocalDateTime;

public record StudentResponse(
    Long id,
    String name,
    String email,
    LocalDateTime createdAt
) {
    public static StudentResponse fromEntity(Student student) {
        return new StudentResponse(
            student.getId(),
            student.getName(),
            student.getEmail(),
            student.getCreatedAt()
        );
    }
}
