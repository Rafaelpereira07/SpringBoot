package com.example.demo.courses;

public record CourseResponse(
        Long id,
        String title,
        String slug,
        String language,
        CourseLevel level,
        String thumbnailUrl,
        Integer durationMinutes
) {
}
