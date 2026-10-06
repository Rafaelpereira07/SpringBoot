package com.example.demo.lessons;

import com.example.demo.courses.Course;
import com.example.demo.courses.CourseService;
import com.example.demo.courses.SlugUtil;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.exceptions.SubscriptionRequiredException;
import com.example.demo.subscriptions.SubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseService courseService;
    private final SubscriptionService subscriptionService;
    private final LessonMapper lessonMapper;

    public LessonService(LessonRepository lessonRepository,
                          CourseService courseService,
                          SubscriptionService subscriptionService,
                          LessonMapper lessonMapper) {
        this.lessonRepository = lessonRepository;
        this.courseService = courseService;
        this.subscriptionService = subscriptionService;
        this.lessonMapper = lessonMapper;
    }

    /**
     * Serves the actual video URL. Requires BOTH an authenticated student
     * (checked upstream via JWT, studentId is never null here) AND an
     * active subscription - possessing a valid token alone is not enough
     * (requirement 11).
     */
    @Transactional(readOnly = true)
    public LessonVideoResponse getVideoForStudent(Long studentId, String courseSlug, String lessonSlug) {
        Lesson lesson = findActiveLesson(courseSlug, lessonSlug);

        if (!subscriptionService.hasActiveSubscription(studentId)) {
            throw new SubscriptionRequiredException("Voce nao possui acesso a este video. E necessaria uma assinatura ativa.");
        }

        return lessonMapper.toVideoResponse(lesson);
    }

    @Transactional(readOnly = true)
    public Lesson findActiveLesson(String courseSlug, String lessonSlug) {
        Course course = courseService.getActiveBySlug(courseSlug);
        return lessonRepository.findByCourseIdAndSlugAndDeletedAtIsNull(course.getId(), lessonSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Aula nao encontrada: " + lessonSlug));
    }

    @Transactional
    public LessonSummaryResponse create(Long courseId, LessonRequest request) {
        Course course = courseService.getByIdForAdmin(courseId);
        String slug = uniqueSlug(course.getId(), SlugUtil.slugify(request.title()));
        Lesson lesson = lessonMapper.fromRequest(request, course, slug);
        Lesson saved = lessonRepository.save(lesson);
        return lessonMapper.toSummary(saved);
    }

    @Transactional
    public LessonSummaryResponse update(Long lessonId, LessonRequest request) {
        Lesson lesson = getByIdForAdmin(lessonId);
        lessonMapper.applyUpdate(lesson, request);
        return lessonMapper.toSummary(lesson);
    }

    /** Soft delete only - preserves progress/certificate history that references this lesson. */
    @Transactional
    public void softDelete(Long lessonId) {
        Lesson lesson = getByIdForAdmin(lessonId);
        if (!lesson.isDeleted()) {
            lesson.setDeletedAt(LocalDateTime.now());
        }
    }

    @Transactional(readOnly = true)
    public Lesson getByIdForAdmin(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aula nao encontrada."));
    }

    private String uniqueSlug(Long courseId, String base) {
        String candidate = base;
        int suffix = 2;
        while (lessonRepository.existsByCourseIdAndSlug(courseId, candidate)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }
}
