package com.example.demo.lessons;

import com.example.demo.courses.Course;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {

    public LessonSummaryResponse toSummary(Lesson lesson) {
        return new LessonSummaryResponse(
                lesson.getId(), lesson.getTitle(), lesson.getSlug(),
                lesson.getThumbnailUrl(), lesson.getDurationSeconds(), lesson.getOrder()
        );
    }

    public LessonVideoResponse toVideoResponse(Lesson lesson) {
        return new LessonVideoResponse(lesson.getId(), lesson.getTitle(), lesson.getVideoUrl(), lesson.getDurationSeconds());
    }

    public Lesson fromRequest(LessonRequest request, Course course, String slug) {
        return Lesson.builder()
                .course(course)
                .title(request.title())
                .slug(slug)
                .description(request.description())
                .thumbnailUrl(request.thumbnailUrl())
                .videoUrl(request.videoUrl())
                .durationSeconds(request.durationSeconds() != null ? request.durationSeconds() : 0)
                .order(request.order() != null ? request.order() : 0)
                .build();
    }

    public void applyUpdate(Lesson lesson, LessonRequest request) {
        lesson.setTitle(request.title());
        lesson.setDescription(request.description());
        lesson.setThumbnailUrl(request.thumbnailUrl());
        lesson.setVideoUrl(request.videoUrl());
        if (request.durationSeconds() != null) {
            lesson.setDurationSeconds(request.durationSeconds());
        }
        if (request.order() != null) {
            lesson.setOrder(request.order());
        }
    }
}
