package com.example.demo.courses;

import com.example.demo.lessons.LessonSummaryResponse;

import java.util.List;

/**
 * Full public course page: description plus lesson names/thumbnails/order -
 * but never video URLs (see requirement 10, LessonSummaryResponse never
 * carries videoUrl).
 */
public record CourseDetailResponse(
        Long id,
        String title,
        String slug,
        String description,
        String language,
        CourseLevel level,
        String thumbnailUrl,
        Integer durationMinutes,
        List<LessonSummaryResponse> lessons
) {
}
