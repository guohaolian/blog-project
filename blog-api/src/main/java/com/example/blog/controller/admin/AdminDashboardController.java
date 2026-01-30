package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.service.AdminDashboardService;
import com.example.blog.vo.admin.AdminDashboardStatsVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/stats")
    public ApiResponse<AdminDashboardStatsVO> stats() {
        return ApiResponse.ok(adminDashboardService.stats());
    }
}
