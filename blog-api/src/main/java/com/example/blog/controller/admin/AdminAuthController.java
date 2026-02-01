package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.admin.AdminLoginRequest;
import com.example.blog.security.AdminPrincipal;
import com.example.blog.service.AdminAuthService;
import com.example.blog.vo.admin.AdminLoginResponse;
import com.example.blog.vo.admin.AdminMeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Admin - Auth", description = "Admin authentication")
@RestController
@RequestMapping("/api/admin/auth")
@Validated
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @Operation(summary = "Admin login", description = "Login by username/password and get JWT token.")
    @PostMapping("/login")
    public ApiResponse<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest req) {
        return ApiResponse.ok(adminAuthService.login(req));
    }

    @Operation(summary = "Get current admin", description = "Get current admin profile by JWT principal.")
    @GetMapping("/me")
    public ApiResponse<AdminMeResponse> me(Authentication authentication) {
        AdminPrincipal principal = (AdminPrincipal) authentication.getPrincipal();
        return ApiResponse.ok(adminAuthService.me(principal.getAdminId()));
    }
}
