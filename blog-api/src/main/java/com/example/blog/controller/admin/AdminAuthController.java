package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.admin.AdminLoginRequest;
import com.example.blog.security.AdminPrincipal;
import com.example.blog.service.AdminAuthService;
import com.example.blog.vo.admin.AdminLoginResponse;
import com.example.blog.vo.admin.AdminMeResponse;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/auth")
@Validated
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    public ApiResponse<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest req) {
        return ApiResponse.ok(adminAuthService.login(req));
    }

    @GetMapping("/me")
    public ApiResponse<AdminMeResponse> me(Authentication authentication) {
        AdminPrincipal principal = (AdminPrincipal) authentication.getPrincipal();
        return ApiResponse.ok(adminAuthService.me(principal.getAdminId()));
    }
}
