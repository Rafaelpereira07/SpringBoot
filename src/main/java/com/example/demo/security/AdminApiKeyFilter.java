package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Guards every {@code /admin/**} route. The professor authenticates with a
 * fixed key sent via the {@code X-Admin-Api-Key} header, checked against
 * {@code ADMIN_API_KEY}. Requests without a matching key are left
 * unauthenticated and rejected by {@code SecurityConfig}'s authorization
 * rules (they never reach a controller).
 */
@Component
public class AdminApiKeyFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Admin-Api-Key";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final AdminProperties adminProperties;

    public AdminApiKeyFilter(AdminProperties adminProperties) {
        this.adminProperties = adminProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String providedKey = request.getHeader(HEADER);
        String configuredKey = adminProperties.getApiKey();

        if (providedKey != null && configuredKey != null && constantTimeEquals(providedKey, configuredKey)) {
            var authentication = new UsernamePasswordAuthenticationToken(
                    "professor", null, List.of(new SimpleGrantedAuthority(ROLE_ADMIN)));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /** Avoids leaking key-length information via short-circuiting string comparison. */
    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
