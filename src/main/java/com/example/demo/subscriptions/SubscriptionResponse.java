package com.example.demo.subscriptions;

import java.time.LocalDateTime;

public record SubscriptionResponse(
        Long id,
        String planName,
        SubscriptionStatus status,
        LocalDateTime startedAt,
        LocalDateTime activatedAt,
        LocalDateTime expiresAt
) {
}
