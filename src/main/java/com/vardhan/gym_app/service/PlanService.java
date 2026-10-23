package com.vardhan.gym_app.service;

import com.vardhan.gym_app.dto.PlanForm;
import com.vardhan.gym_app.entity.Plan;
import com.vardhan.gym_app.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    public boolean nameExists(String name) {
        return planRepository.existsByNameIgnoreCase(name.trim());
    }

    public Plan createPlan(PlanForm form) {
        Plan plan = Plan.builder()
                .name(form.getName().trim())
                .durationInMonths(form.getDurationInMonths())
                .price(form.getPrice())
                .description(form.getDescription())
                .active(true)
                .build();

        return planRepository.save(plan);
    }

    public List<Plan> getActivePlans() {
        return planRepository.findByActiveTrueOrderByDurationInMonthsAsc();
    }

    public void toggleActive(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
        plan.setActive(!plan.isActive());
        planRepository.save(plan);
    }
}