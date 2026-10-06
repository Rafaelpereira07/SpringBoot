package com.example.demo.students;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInSeconds,
        StudentResponse student
) {
    public static LoginResponse of(String token, long expiresInSeconds, StudentResponse student) {
        return new LoginResponse(token, "Bearer", expiresInSeconds, student);
    }
}
