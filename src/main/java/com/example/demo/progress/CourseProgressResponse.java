package com.example.demo.progress;

public record CourseProgressResponse(
        Long courseId,
        long totalLessons,
        long completedLessons,
        double percentage,
        boolean courseCompleted
) {
}
