package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Reads the {@code Authorization: Bearer <token>} header, validates the JWT
 * and, if valid, populates the security context with the student id as the
 * principal. Does NOT by itself decide whether the student may watch a
 * given video - that additional subscription check happens in
 * {@code LessonService} (see requirement: "Nao permitir acesso ao video
 * somente porque o usuario possui um token valido").
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HEADER);

        if (header != null && header.startsWith(PREFIX)) {
            String token = header.substring(PREFIX.length());
            try {
                if (jwtService.isValid(token)) {
                    Long studentId = jwtService.extractStudentId(token);
                    var authentication = new UsernamePasswordAuthenticationToken(studentId, null, List.of());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ignored) {
                // Malformed/invalid token: leave the context unauthenticated;
                // Spring Security will reject access to protected routes.
            }
        }

        filterChain.doFilter(request, response);
    }
}
