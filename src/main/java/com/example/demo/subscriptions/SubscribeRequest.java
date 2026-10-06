package com.example.demo.subscriptions;

import com.example.demo.plans.PlanType;
import jakarta.validation.constraints.NotNull;

public record SubscribeRequest(
        @NotNull(message = "Tipo de plano e obrigatorio.")
        PlanType planType
) {
}
