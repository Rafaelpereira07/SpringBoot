package com.example.demo.progress;

import jakarta.validation.constraints.NotBlank;

public record CompleteLessonRequest(
        @NotBlank(message = "Slug do curso e obrigatorio.")
        String courseSlug,

        @NotBlank(message = "Slug da aula e obrigatorio.")
        String lessonSlug
) {
}
