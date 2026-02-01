package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.admin.AdminUserCreateRequest;
import com.example.blog.dto.admin.AdminUserResetPasswordRequest;
import com.example.blog.dto.admin.AdminUserStatusRequest;
import com.example.blog.service.AdminUserService;
import com.example.blog.vo.admin.AdminUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Tag(name = "Admin - Admin Users", description = "Admin user management")
@RestController
@RequestMapping("/api/admin/admins")
@Validated
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @Operation(summary = "List admins")
    @GetMapping
    public ApiResponse<List<AdminUserVO>> list() {
        return ApiResponse.ok(adminUserService.list());
    }

    @Operation(summary = "Create admin")
    @PostMapping
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody AdminUserCreateRequest req) {
        return ApiResponse.ok(adminUserService.create(req));
    }

    @Operation(summary = "Reset admin password")
    @PutMapping("/{id}/reset-password")
    public ApiResponse<Map<String, Object>> resetPassword(@PathVariable("id") Long id,
                                                          @Valid @RequestBody AdminUserResetPasswordRequest req) {
        return ApiResponse.ok(adminUserService.resetPassword(id, req));
    }

    @Operation(summary = "Update admin status", description = "Enable or disable an admin user.")
    @PutMapping("/{id}/status")
    public ApiResponse<Map<String, Object>> status(@PathVariable("id") Long id,
                                                   @Valid @RequestBody AdminUserStatusRequest req) {
        return ApiResponse.ok(adminUserService.updateStatus(id, req));
    }
}
