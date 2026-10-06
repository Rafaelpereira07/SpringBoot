package com.example.demo.plans;

/**
 * The three mandatory plan types. VITALICIO is handled explicitly wherever
 * expiration is computed (see SubscriptionService), never inferred from a
 * magic duration value such as "durationDays == null" alone.
 */
public enum PlanType {
    MENSAL(30),
    ANUAL(365),
    VITALICIO(null);

    private final Integer durationDays;

    PlanType(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public boolean isLifetime() {
        return this == VITALICIO;
    }
}
