package com.example.demo.certificates;

import java.time.LocalDate;

public record CertificateValidationResponse(
        boolean valid,
        String student,
        String course,
        LocalDate completedAt,
        Integer courseDurationMinutes
) {
    public static CertificateValidationResponse invalid() {
        return new CertificateValidationResponse(false, null, null, null, null);
    }
}
