package com.example.demo.lessons;

import com.example.demo.security.CurrentStudentProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Aulas (publico/protegido)")
public class LessonController {

    private final LessonService lessonService;
    private final CurrentStudentProvider currentStudentProvider;

    public LessonController(LessonService lessonService, CurrentStudentProvider currentStudentProvider) {
        this.lessonService = lessonService;
        this.currentStudentProvider = currentStudentProvider;
    }

    /**
     * Protected route (see SecurityConfig): serves the actual video. An
     * authenticated student without an active subscription still reaches
     * this method (the JWT is valid) but LessonService rejects it with
     * SubscriptionRequiredException, matching the "SUBSCRIPTION_REQUIRED"
     * response shape from requirement 10.
     */
    @GetMapping("/courses/{courseSlug}/{lessonSlug}")
    public ResponseEntity<LessonVideoResponse> watch(@PathVariable String courseSlug, @PathVariable String lessonSlug) {
        Long studentId = currentStudentProvider.requireStudentId();
        return ResponseEntity.ok(lessonService.getVideoForStudent(studentId, courseSlug, lessonSlug));
    }
}
