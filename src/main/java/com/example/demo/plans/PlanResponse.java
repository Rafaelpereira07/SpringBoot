package com.example.demo.plans;

import java.math.BigDecimal;

public record PlanResponse(
        Long id,
        String name,
        PlanType type,
        String description,
        Integer durationDays,
        BigDecimal price,
        boolean lifetime
) {
}
