package com.example.demo.progress;

import com.example.demo.certificates.CertificateService;
import com.example.demo.courses.Course;
import com.example.demo.courses.CourseService;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.exceptions.SubscriptionRequiredException;
import com.example.demo.lessons.Lesson;
import com.example.demo.lessons.LessonRepository;
import com.example.demo.lessons.LessonService;
import com.example.demo.students.Student;
import com.example.demo.students.StudentRepository;
import com.example.demo.subscriptions.SubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;
    private final LessonService lessonService;
    private final CourseService courseService;
    private final SubscriptionService subscriptionService;
    private final CertificateService certificateService;

    public ProgressService(ProgressRepository progressRepository,
                            LessonRepository lessonRepository,
                            StudentRepository studentRepository,
                            LessonService lessonService,
                            CourseService courseService,
                            SubscriptionService subscriptionService,
                            CertificateService certificateService) {
        this.progressRepository = progressRepository;
        this.lessonRepository = lessonRepository;
        this.studentRepository = studentRepository;
        this.lessonService = lessonService;
        this.courseService = courseService;
        this.subscriptionService = subscriptionService;
        this.certificateService = certificateService;
    }

    /**
     * Registers a lesson as completed for the student. Requires an active
     * subscription, same rule that gates the video itself. Progress is
     * never deleted afterward, even if the subscription later expires
     * (requirement: "O progresso do aluno nao pode ser apagado quando a
     * assinatura expira").
     */
    @Transactional
    public ProgressResponse markLessonCompleted(Long studentId, CompleteLessonRequest request) {
        if (!subscriptionService.hasActiveSubscription(studentId)) {
            throw new SubscriptionRequiredException("E necessaria uma assinatura ativa para registrar progresso.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno nao encontrado."));
        Lesson lesson = lessonService.findActiveLesson(request.courseSlug(), request.lessonSlug());

        Progress progress = progressRepository.findByStudentIdAndLessonId(studentId, lesson.getId())
                .orElseGet(() -> Progress.builder().student(student).lesson(lesson).build());

        if (!Boolean.TRUE.equals(progress.getCompleted())) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
        }
        Progress saved = progressRepository.save(progress);

        maybeIssueCertificate(student, lesson.getCourse());

        return new ProgressResponse(lesson.getId(), saved.getCompleted(), saved.getCompletedAt());
    }

    @Transactional(readOnly = true)
    public CourseProgressResponse getCourseProgress(Long studentId, String courseSlug) {
        Course course = courseService.getActiveBySlug(courseSlug);
        long total = lessonRepository.countByCourseIdAndDeletedAtIsNull(course.getId());
        long completed = progressRepository.countByStudentIdAndLesson_Course_IdAndCompletedTrue(studentId, course.getId());
        double percentage = total == 0 ? 0.0 : (completed * 100.0) / total;
        return new CourseProgressResponse(course.getId(), total, completed, percentage, total > 0 && completed >= total);
    }

    /** Generates the certificate automatically once every active lesson in the course is completed. */
    private void maybeIssueCertificate(Student student, Course course) {
        long total = lessonRepository.countByCourseIdAndDeletedAtIsNull(course.getId());
        long completed = progressRepository.countByStudentIdAndLesson_Course_IdAndCompletedTrue(student.getId(), course.getId());
        if (total > 0 && completed >= total) {
            certificateService.issueIfNotExists(student, course, course.getDurationMinutes());
        }
    }
}
