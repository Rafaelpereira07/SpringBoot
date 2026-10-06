package com.example.demo.plans;

import com.example.demo.config.PlanPricingProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PlanMapper {

    private final PlanPricingProperties pricingProperties;

    public PlanMapper(PlanPricingProperties pricingProperties) {
        this.pricingProperties = pricingProperties;
    }

    public PlanResponse toResponse(Plan plan) {
        BigDecimal effectivePrice = resolvePrice(plan);
        return new PlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getType(),
                plan.getDescription(),
                plan.getDurationDays(),
                effectivePrice,
                plan.getType().isLifetime()
        );
    }

    /** The configured price (per environment) takes precedence over the seeded DB value. */
    private BigDecimal resolvePrice(Plan plan) {
        return switch (plan.getType()) {
            case MENSAL -> pricingProperties.getMensal().getPrice();
            case ANUAL -> pricingProperties.getAnual().getPrice();
            case VITALICIO -> pricingProperties.getVitalicio().getPrice();
        };
    }
}
