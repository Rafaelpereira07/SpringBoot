package com.example.demo.students;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentRegisterRequest(
        @NotBlank(message = "Nome e obrigatorio.")
        @Size(max = 150, message = "Nome deve ter no maximo 150 caracteres.")
        String name,

        @NotBlank(message = "E-mail e obrigatorio.")
        @Email(message = "E-mail invalido.")
        @Size(max = 180, message = "E-mail deve ter no maximo 180 caracteres.")
        String email,

        @NotBlank(message = "Senha e obrigatoria.")
        @Size(min = 8, message = "Senha deve ter no minimo 8 caracteres.")
        String password
) {
}
