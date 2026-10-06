package com.example.demo.plans;

import com.example.demo.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanMapper planMapper;

    public PlanService(PlanRepository planRepository, PlanMapper planMapper) {
        this.planRepository = planRepository;
        this.planMapper = planMapper;
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> listActive() {
        return planRepository.findByDeletedAtIsNull().stream()
                .map(planMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Plan getActiveByType(PlanType type) {
        return planRepository.findByTypeAndDeletedAtIsNull(type)
                .orElseThrow(() -> new ResourceNotFoundException("Plano indisponivel: " + type));
    }
}
