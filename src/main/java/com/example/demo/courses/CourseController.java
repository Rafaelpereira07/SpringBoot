package com.example.demo.courses;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Fully public: catalog browsing needs no authentication (requirement 10). */
@RestController
@RequestMapping("/courses")
@Tag(name = "Cursos (publico)")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> list() {
        return ResponseEntity.ok(courseService.listPublished());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<CourseDetailResponse> detail(@PathVariable String slug) {
        return ResponseEntity.ok(courseService.getPublishedDetailBySlug(slug));
    }
}
