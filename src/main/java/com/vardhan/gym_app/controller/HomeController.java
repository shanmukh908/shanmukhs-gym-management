package com.vardhan.gym_app.controller;

import com.vardhan.gym_app.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DashboardService dashboardService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("dashboard", dashboardService.getDashboard());
        return "index";
    }
}