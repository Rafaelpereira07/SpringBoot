package com.example.demo.lessons;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Aulas (admin)")
public class AdminLessonController {

    private final LessonService lessonService;

    public AdminLessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping("/admin/courses/{courseId}/lessons")
    public ResponseEntity<LessonSummaryResponse> create(@PathVariable Long courseId, @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.create(courseId, request));
    }

    @PutMapping("/admin/lessons/{id}")
    public ResponseEntity<LessonSummaryResponse> update(@PathVariable Long id, @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonService.update(id, request));
    }

    @DeleteMapping("/admin/lessons/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
