package com.example.demo.courses;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseRequest(
        @NotBlank(message = "Titulo e obrigatorio.")
        @Size(max = 150, message = "Titulo deve ter no maximo 150 caracteres.")
        String title,

        @NotBlank(message = "Descricao e obrigatoria.")
        String description,

        String language,

        @NotNull(message = "Nivel e obrigatorio.")
        CourseLevel level,

        String thumbnailUrl,

        @Min(value = 0, message = "Duracao nao pode ser negativa.")
        Integer durationMinutes
) {
}
