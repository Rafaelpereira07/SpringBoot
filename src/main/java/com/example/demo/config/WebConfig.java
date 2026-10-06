package com.example.demo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers cross-cutting servlet filters. The artificial delay is applied
 * to every request ({@code /*}) so its behaviour is documented in one
 * place instead of being duplicated per-controller.
 */
@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<ArtificialDelayFilter> artificialDelayFilter(ArtificialDelayProperties properties) {
        FilterRegistrationBean<ArtificialDelayFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ArtificialDelayFilter(properties));
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        registration.setName("artificialDelayFilter");
        return registration;
    }
}
