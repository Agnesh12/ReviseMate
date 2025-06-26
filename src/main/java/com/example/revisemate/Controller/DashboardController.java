package com.example.revisemate.Controller;

import com.example.revisemate.DTO.DashboardDTO;
import com.example.revisemate.Service.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard") @RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public DashboardDTO.DashboardStats stats(@AuthenticationPrincipal Long userId) {
        return dashboardService.stats(userId);
    }
}

