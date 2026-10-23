package com.vardhan.gym_app.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PlanForm {

    @NotBlank(message = "Plan name is required")
    @Size(max = 50, message = "Plan name must be 50 characters or fewer")
    private String name;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 month")
    @Max(value = 36, message = "Duration cannot exceed 36 months")
    private Integer durationInMonths;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal price;

    @Size(max = 255, message = "Description must be 255 characters or fewer")
    private String description;
}