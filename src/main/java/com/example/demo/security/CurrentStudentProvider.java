package com.example.demo.security;

import com.example.demo.exceptions.InvalidCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Reads the authenticated student's id out of the security context.
 * Controllers use this instead of trusting any id supplied by the client,
 * so a student can never act on another student's data.
 */
@Component
public class CurrentStudentProvider {

    public Long requireStudentId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;
        if (principal instanceof Long studentId) {
            return studentId;
        }
        throw new InvalidCredentialsException("Autenticacao de aluno necessaria.");
    }
}
