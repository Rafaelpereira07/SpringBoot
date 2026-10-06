package com.example.demo.plans;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByDeletedAtIsNull();
    Optional<Plan> findByTypeAndDeletedAtIsNull(PlanType type);
}
