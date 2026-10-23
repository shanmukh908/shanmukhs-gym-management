package com.vardhan.gym_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalModelAttributes {

    @Value("${app.gym.name}")
    private String gymName;

    @ModelAttribute("gymName")
    public String gymName() {
        return gymName;
    }

    @ModelAttribute("currentUserName")
    public String currentUserName(Principal principal) {
        return principal != null ? principal.getName() : null;
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}