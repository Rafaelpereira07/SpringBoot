package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Two independent authentication mechanisms coexist:
 *  - Students authenticate with a JWT bearer token ({@link JwtAuthenticationFilter}).
 *  - The professor authenticates on /admin/** with a fixed API key
 *    ({@link AdminApiKeyFilter}), per requirement 11.
 * Public browsing routes (home, plans, course/lesson listings, certificate
 * PDF + validation) stay open with no authentication at all.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AdminApiKeyFilter adminApiKeyFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AdminApiKeyFilter adminApiKeyFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.adminApiKeyFilter = adminApiKeyFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public: docs, auth, browsing, certificate reading/validation
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/docs/**", "/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/auth/**", "/students/register").permitAll()
                        .requestMatchers("/plans/**").permitAll()
                        .requestMatchers("/certificates/*/validate", "/certificates/*").permitAll()
                        // Simulated payment confirmation callback - see SubscriptionController.
                        // Reachable without a student session (that's the point of "reading" the
                        // QR code), but it can only ever act on the one unguessable payment code
                        // it's given.
                        .requestMatchers(HttpMethod.POST, "/subscriptions/*/confirm").permitAll()
                        // Course catalog and a single course's page (description + lesson
                        // names/thumbnails) are public. A specific lesson video
                        // ("/courses/{course}/{lesson}") is intentionally NOT matched
                        // here (it has one extra path segment) so it falls through to
                        // "anyRequest().authenticated()" below - see requirement 10.
                        .requestMatchers(HttpMethod.GET, "/courses").permitAll()
                        .requestMatchers(HttpMethod.GET, "/courses/*").permitAll()
                        // Admin: requires the ROLE_ADMIN authority granted by AdminApiKeyFilter
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Everything else requires an authenticated student
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(adminApiKeyFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
