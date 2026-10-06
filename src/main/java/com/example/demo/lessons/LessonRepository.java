package com.example.demo.lessons;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourseIdAndDeletedAtIsNullOrderByOrderAsc(Long courseId);
    Optional<Lesson> findByCourseIdAndSlugAndDeletedAtIsNull(Long courseId, String slug);
    long countByCourseIdAndDeletedAtIsNull(Long courseId);
    boolean existsByCourseIdAndSlug(Long courseId, String slug);
}
