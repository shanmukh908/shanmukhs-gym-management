package com.vardhan.gym_app.controller;

import com.vardhan.gym_app.dto.EnrollmentForm;
import com.vardhan.gym_app.entity.Enrollment;
import com.vardhan.gym_app.exception.BusinessRuleException;
import com.vardhan.gym_app.service.EnrollmentService;
import com.vardhan.gym_app.service.MemberService;
import com.vardhan.gym_app.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final MemberService memberService;
    private final PlanService planService;

    @GetMapping
    public String listEnrollments(Model model) {
        model.addAttribute("enrollments", enrollmentService.getAllEnrollments());
        return "enrollments/list";
    }

    @GetMapping("/new")
    public String showForm(@RequestParam(required = false) Long memberId,
                           @RequestParam(required = false) Long planId,
                           @RequestParam(required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                           Model model) {
        EnrollmentForm form = new EnrollmentForm();
        form.setMemberId(memberId);
        form.setPlanId(planId);
        form.setStartDate(startDate != null ? startDate : LocalDate.now());

        model.addAttribute("enrollmentForm", form);
        loadDropdowns(model);
        return "enrollments/form";
    }

    @PostMapping
    public String enroll(@Valid @ModelAttribute("enrollmentForm") EnrollmentForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (!bindingResult.hasErrors()) {
            try {
                Enrollment enrollment = enrollmentService.enroll(form);

                String endText = enrollment.getEndDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
                redirectAttributes.addFlashAttribute("successMessage",
                        enrollment.getMember().getFullName() + " enrolled in the "
                                + enrollment.getPlan().getName() + " plan until " + endText + ".");
                return "redirect:/enrollments";

            } catch (BusinessRuleException ex) {
                bindingResult.reject("businessRule", ex.getMessage());
            }
        }

        loadDropdowns(model);
        return "enrollments/form";
    }

    private void loadDropdowns(Model model) {
        model.addAttribute("members", memberService.getMembers(null));
        model.addAttribute("plans", planService.getActivePlans());
    }
}