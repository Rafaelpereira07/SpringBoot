package com.example.demo.courses;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.lessons.Lesson;
import com.example.demo.lessons.LessonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, LessonRepository lessonRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.courseMapper = courseMapper;
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> listPublished() {
        return courseRepository.findByDeletedAtIsNull().stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseDetailResponse getPublishedDetailBySlug(String slug) {
        Course course = getActiveBySlug(slug);
        List<Lesson> lessons = lessonRepository.findByCourseIdAndDeletedAtIsNullOrderByOrderAsc(course.getId());
        return courseMapper.toDetailResponse(course, lessons);
    }

    @Transactional(readOnly = true)
    public Course getActiveBySlug(String slug) {
        return courseRepository.findBySlugAndDeletedAtIsNull(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Curso nao encontrado: " + slug));
    }

    @Transactional(readOnly = true)
    public Course getByIdForAdmin(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso nao encontrado."));
    }

    @Transactional
    public CourseResponse create(CourseRequest request) {
        String slug = uniqueSlug(SlugUtil.slugify(request.title()));
        Course course = courseMapper.fromRequest(request, slug);
        Course saved = courseRepository.save(course);
        return courseMapper.toResponse(saved);
    }

    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = getByIdForAdmin(id);
        courseMapper.applyUpdate(course, request);
        return courseMapper.toResponse(course);
    }

    /**
     * Soft delete: sets deletedAt instead of physically removing the row, so
     * existing progress and certificates referencing this course remain
     * intact (requirement 16).
     */
    @Transactional
    public void softDelete(Long id) {
        Course course = getByIdForAdmin(id);
        if (!course.isDeleted()) {
            course.setDeletedAt(LocalDateTime.now());
        }
    }

    private String uniqueSlug(String base) {
        String candidate = base;
        int suffix = 2;
        while (courseRepository.existsBySlug(candidate)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }
}
