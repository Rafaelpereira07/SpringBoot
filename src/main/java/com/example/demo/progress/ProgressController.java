package com.example.demo.progress;

import com.example.demo.security.CurrentStudentProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** All routes here require an authenticated student (see SecurityConfig). */
@RestController
@RequestMapping("/progress")
@Tag(name = "Progresso")
public class ProgressController {

    private final ProgressService progressService;
    private final CurrentStudentProvider currentStudentProvider;

    public ProgressController(ProgressService progressService, CurrentStudentProvider currentStudentProvider) {
        this.progressService = progressService;
        this.currentStudentProvider = currentStudentProvider;
    }

    @PostMapping("/complete")
    public ResponseEntity<ProgressResponse> complete(@Valid @RequestBody CompleteLessonRequest request) {
        Long studentId = currentStudentProvider.requireStudentId();
        return ResponseEntity.ok(progressService.markLessonCompleted(studentId, request));
    }

    @GetMapping("/courses/{courseSlug}")
    public ResponseEntity<CourseProgressResponse> courseProgress(@PathVariable String courseSlug) {
        Long studentId = currentStudentProvider.requireStudentId();
        return ResponseEntity.ok(progressService.getCourseProgress(studentId, courseSlug));
    }
}
