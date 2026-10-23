package com.vardhan.gym_app.controller;

import com.vardhan.gym_app.dto.PlanForm;
import com.vardhan.gym_app.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public String listPlans(Model model) {
        model.addAttribute("plans", planService.getAllPlans());
        return "plans/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("planForm", new PlanForm());
        return "plans/form";
    }

    @PostMapping
    public String createPlan(@Valid @ModelAttribute("planForm") PlanForm planForm,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        if (planForm.getName() != null && !planForm.getName().isBlank()
                && planService.nameExists(planForm.getName())) {
            bindingResult.rejectValue("name", "duplicate", "A plan with this name already exists");
        }

        if (bindingResult.hasErrors()) {
            return "plans/form";
        }

        planService.createPlan(planForm);
        redirectAttributes.addFlashAttribute("successMessage", "Plan added successfully!");
        return "redirect:/plans";
    }

    @PostMapping("/{id}/toggle")
    public String togglePlan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        planService.toggleActive(id);
        redirectAttributes.addFlashAttribute("successMessage", "Plan status updated.");
        return "redirect:/plans";
    }
}