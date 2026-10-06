package com.example.demo.courses;

import com.example.demo.lessons.Lesson;
import com.example.demo.lessons.LessonMapper;
import com.example.demo.lessons.LessonSummaryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseMapper {

    private final LessonMapper lessonMapper;

    public CourseMapper(LessonMapper lessonMapper) {
        this.lessonMapper = lessonMapper;
    }

    public CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(), course.getTitle(), course.getSlug(), course.getLanguage(),
                course.getLevel(), course.getThumbnailUrl(), course.getDurationMinutes()
        );
    }

    public CourseDetailResponse toDetailResponse(Course course, List<Lesson> lessons) {
        List<LessonSummaryResponse> lessonSummaries = lessons.stream()
                .map(lessonMapper::toSummary)
                .toList();
        return new CourseDetailResponse(
                course.getId(), course.getTitle(), course.getSlug(), course.getDescription(),
                course.getLanguage(), course.getLevel(), course.getThumbnailUrl(),
                course.getDurationMinutes(), lessonSummaries
        );
    }

    public Course fromRequest(CourseRequest request, String slug) {
        return Course.builder()
                .title(request.title())
                .slug(slug)
                .description(request.description())
                .language(request.language())
                .level(request.level())
                .thumbnailUrl(request.thumbnailUrl())
                .durationMinutes(request.durationMinutes() != null ? request.durationMinutes() : 0)
                .build();
    }

    public void applyUpdate(Course course, CourseRequest request) {
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setLanguage(request.language());
        course.setLevel(request.level());
        course.setThumbnailUrl(request.thumbnailUrl());
        if (request.durationMinutes() != null) {
            course.setDurationMinutes(request.durationMinutes());
        }
    }
}
