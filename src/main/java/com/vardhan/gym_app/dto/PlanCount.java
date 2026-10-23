package com.vardhan.gym_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlanCount {
    private String planName;
    private Long count;
}