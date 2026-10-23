package com.vardhan.gym_app.controller;

import com.vardhan.gym_app.dto.MemberForm;
import com.vardhan.gym_app.entity.Gender;
import com.vardhan.gym_app.exception.BusinessRuleException;
import com.vardhan.gym_app.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @ModelAttribute("genders")
    public Gender[] genders() {
        return Gender.values();
    }

    @GetMapping
    public String listMembers(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("members", memberService.getMembers(keyword));
        model.addAttribute("keyword", keyword);
        return "members/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/form";
    }

    @PostMapping
    public String createMember(@Valid @ModelAttribute("memberForm") MemberForm memberForm,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        checkDuplicateEmail(memberForm, null, bindingResult);

        if (bindingResult.hasErrors()) {
            return "members/form";
        }

        memberService.createMember(memberForm);
        redirectAttributes.addFlashAttribute("successMessage", "Member added successfully!");
        return "redirect:/members";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("memberForm", memberService.toForm(memberService.getMember(id)));
        model.addAttribute("memberId", id);
        return "members/form";
    }

    @PostMapping("/{id}")
    public String updateMember(@PathVariable Long id,
                               @Valid @ModelAttribute("memberForm") MemberForm memberForm,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        checkDuplicateEmail(memberForm, id, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("memberId", id);
            return "members/form";
        }

        memberService.updateMember(id, memberForm);
        redirectAttributes.addFlashAttribute("successMessage", "Member updated successfully!");
        return "redirect:/members";
    }

    @PostMapping("/{id}/delete")
    public String deleteMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            memberService.deleteMember(id);
            redirectAttributes.addFlashAttribute("successMessage", "Member deleted.");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/members";
    }

    private void checkDuplicateEmail(MemberForm form, Long currentMemberId, BindingResult bindingResult) {
        if (form.getEmail() != null && !form.getEmail().isBlank()
                && memberService.emailTaken(form.getEmail(), currentMemberId)) {
            bindingResult.rejectValue("email", "duplicate", "A member with this email already exists");
        }
    }
}