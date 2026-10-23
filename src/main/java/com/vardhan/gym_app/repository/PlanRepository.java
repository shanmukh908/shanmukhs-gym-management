package com.vardhan.gym_app.repository;

import com.vardhan.gym_app.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    boolean existsByNameIgnoreCase(String name);
    List<Plan> findByActiveTrueOrderByDurationInMonthsAsc();
}