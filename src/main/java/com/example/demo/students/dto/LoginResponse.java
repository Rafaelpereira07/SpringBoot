package com.example.demo.students.dto;

public record LoginResponse(
    String message,
    String token,
    StudentResponse student
) {
}
