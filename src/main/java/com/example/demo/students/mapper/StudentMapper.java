package com.example.demo.students.mapper;

import com.example.demo.students.dto.StudentResponse;
import com.example.demo.students.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentResponse toResponse(Student student) {
        return StudentResponse.fromEntity(student);
    }
}
