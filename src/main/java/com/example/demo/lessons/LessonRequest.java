package com.example.demo.lessons;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record LessonRequest(
        @NotBlank(message = "Titulo e obrigatorio.")
        String title,

        String description,

        String thumbnailUrl,

        @NotBlank(message = "URL do video e obrigatoria.")
        String videoUrl,

        @Min(value = 0, message = "Duracao nao pode ser negativa.")
        Integer durationSeconds,

        @Min(value = 0, message = "Ordem nao pode ser negativa.")
        Integer order
) {
}
