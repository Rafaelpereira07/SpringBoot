package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Applies a single, centralized artificial delay to every request that
 * reaches the application, simulating realistic network latency so the
 * frontend's loading states can be exercised.
 *
 * This is a deliberate architectural choice: rather than sprinkling
 * {@code Thread.sleep()} calls across controllers (which would duplicate
 * logic and be easy to forget on new endpoints), a single servlet filter
 * intercepts every request exactly once, before any controller logic runs.
 * It can be disabled entirely via {@code app.artificial-delay.enabled=false}
 * (this is done automatically in the "test" profile) so it never slows
 * down automated tests.
 *
 * The delay runs before the controller executes, so it never masks or
 * interferes with exception handling performed downstream by
 * {@code GlobalExceptionHandler}.
 */
public class ArtificialDelayFilter extends OncePerRequestFilter {

    private final ArtificialDelayProperties properties;

    public ArtificialDelayFilter(ArtificialDelayProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        if (properties.isEnabled() && properties.getMilliseconds() > 0) {
            try {
                Thread.sleep(properties.getMilliseconds());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        filterChain.doFilter(request, response);
    }
}
