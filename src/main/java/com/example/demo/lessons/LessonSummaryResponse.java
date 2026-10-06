package com.example.demo.lessons;

/** Public info about a lesson - name, thumbnail, duration, order. No video URL. */
public record LessonSummaryResponse(
        Long id,
        String title,
        String slug,
        String thumbnailUrl,
        Integer durationSeconds,
        Integer order
) {
}
