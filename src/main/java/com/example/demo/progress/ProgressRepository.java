package com.example.demo.progress;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    Optional<Progress> findByStudentIdAndLessonId(Long studentId, Long lessonId);
    List<Progress> findByStudentIdAndLesson_Course_Id(Long studentId, Long courseId);
    long countByStudentIdAndLesson_Course_IdAndCompletedTrue(Long studentId, Long courseId);
}
