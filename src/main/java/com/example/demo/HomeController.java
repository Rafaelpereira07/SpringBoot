package com.example.demo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Public root route (see requirement 15 - "/" -> Home), mostly useful as a health/landing check. */
@RestController
@Tag(name = "Home")
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "name", "API de cursos do Wagner",
                "docs", "/docs",
                "status", "ok"
        );
    }
}
