package com.example.demo.courses;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByDeletedAtIsNull();
    Optional<Course> findBySlugAndDeletedAtIsNull(String slug);
    boolean existsBySlug(String slug);
}
