package com.example.demo.students;

import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentResponse toResponse(Student student) {
        return new StudentResponse(student.getId(), student.getName(), student.getEmail(), student.getCreatedAt());
    }
}
