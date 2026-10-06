package com.example.demo.progress;

import com.example.demo.certificates.CertificateService;
import com.example.demo.courses.Course;
import com.example.demo.courses.CourseService;
import com.example.demo.exceptions.SubscriptionRequiredException;
import com.example.demo.lessons.Lesson;
import com.example.demo.lessons.LessonRepository;
import com.example.demo.lessons.LessonService;
import com.example.demo.students.Student;
import com.example.demo.students.StudentRepository;
import com.example.demo.subscriptions.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the two most important progress rules: the subscription
 * gate, and automatic (idempotent) certificate issuance once every lesson
 * of a course has been completed.
 */
@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock private ProgressRepository progressRepository;
    @Mock private LessonRepository lessonRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private LessonService lessonService;
    @Mock private CourseService courseService;
    @Mock private SubscriptionService subscriptionService;
    @Mock private CertificateService certificateService;

    private ProgressService progressService;

    @BeforeEach
    void setUp() {
        progressService = new ProgressService(progressRepository, lessonRepository, studentRepository,
                lessonService, courseService, subscriptionService, certificateService);
    }

    @Test
    void markingALessonCompleteWithoutAnActiveSubscriptionIsRejected() {
        when(subscriptionService.hasActiveSubscription(1L)).thenReturn(false);

        CompleteLessonRequest request = new CompleteLessonRequest("curso", "aula");

        assertThatThrownBy(() -> progressService.markLessonCompleted(1L, request))
                .isInstanceOf(SubscriptionRequiredException.class);
    }

    @Test
    void completingTheLastLessonIssuesACertificateExactlyOnce() {
        Student student = Student.builder().id(1L).name("Joana").email("joana@example.com").build();
        Course course = Course.builder().id(10L).title("Java Basico").durationMinutes(120).build();
        Lesson lesson = Lesson.builder().id(100L).course(course).build();

        when(subscriptionService.hasActiveSubscription(1L)).thenReturn(true);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(lessonService.findActiveLesson("java-basico", "aula-final")).thenReturn(lesson);
        when(progressRepository.findByStudentIdAndLessonId(1L, 100L)).thenReturn(Optional.empty());
        when(progressRepository.save(any(Progress.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // All 3 lessons of the course are now completed.
        when(lessonRepository.countByCourseIdAndDeletedAtIsNull(10L)).thenReturn(3L);
        when(progressRepository.countByStudentIdAndLesson_Course_IdAndCompletedTrue(1L, 10L)).thenReturn(3L);

        CompleteLessonRequest request = new CompleteLessonRequest("java-basico", "aula-final");
        ProgressResponse response = progressService.markLessonCompleted(1L, request);

        assertThat(response.completed()).isTrue();
        verify(certificateService, times(1)).issueIfNotExists(student, course, 120);
    }

    @Test
    void completingANonFinalLessonDoesNotIssueACertificate() {
        Student student = Student.builder().id(1L).name("Joana").email("joana@example.com").build();
        Course course = Course.builder().id(10L).title("Java Basico").durationMinutes(120).build();
        Lesson lesson = Lesson.builder().id(101L).course(course).build();

        when(subscriptionService.hasActiveSubscription(1L)).thenReturn(true);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(lessonService.findActiveLesson("java-basico", "aula-um")).thenReturn(lesson);
        when(progressRepository.findByStudentIdAndLessonId(1L, 101L)).thenReturn(Optional.empty());
        when(progressRepository.save(any(Progress.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(lessonRepository.countByCourseIdAndDeletedAtIsNull(10L)).thenReturn(3L);
        when(progressRepository.countByStudentIdAndLesson_Course_IdAndCompletedTrue(1L, 10L)).thenReturn(1L);

        progressService.markLessonCompleted(1L, new CompleteLessonRequest("java-basico", "aula-um"));

        verify(certificateService, never()).issueIfNotExists(any(), any(), anyInt());
    }

    @Test
    void courseProgressPercentageIsComputedFromCompletedLessons() {
        Course course = Course.builder().id(10L).title("Java Basico").build();
        when(courseService.getActiveBySlug("java-basico")).thenReturn(course);
        when(lessonRepository.countByCourseIdAndDeletedAtIsNull(10L)).thenReturn(4L);
        when(progressRepository.countByStudentIdAndLesson_Course_IdAndCompletedTrue(1L, 10L)).thenReturn(2L);

        CourseProgressResponse response = progressService.getCourseProgress(1L, "java-basico");

        assertThat(response.percentage()).isEqualTo(50.0);
        assertThat(response.courseCompleted()).isFalse();
    }
}
