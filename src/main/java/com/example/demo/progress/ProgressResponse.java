package com.example.demo.progress;

import java.time.LocalDateTime;

public record ProgressResponse(
        Long lessonId,
        boolean completed,
        LocalDateTime completedAt
) {
}
