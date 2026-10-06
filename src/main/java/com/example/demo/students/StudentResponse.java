package com.example.demo.students;

import java.time.LocalDateTime;

/** Never includes the password hash - see requirement "Nao retornar a senha nas respostas". */
public record StudentResponse(
        Long id,
        String name,
        String email,
        LocalDateTime createdAt
) {
}
