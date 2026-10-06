package com.example.demo.lessons;

/** Returned only after the caller is authenticated AND holds an active subscription. */
public record LessonVideoResponse(
        Long id,
        String title,
        String videoUrl,
        Integer durationSeconds
) {
}
