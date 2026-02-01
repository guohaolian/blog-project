package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.admin.SiteSettingRequest;
import com.example.blog.service.SiteSettingService;
import com.example.blog.vo.SiteSettingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Admin - Site", description = "Site settings management")
@RestController
@RequestMapping("/api/admin/site")
@Validated
public class AdminSiteController {

    private final SiteSettingService siteSettingService;

    public AdminSiteController(SiteSettingService siteSettingService) {
        this.siteSettingService = siteSettingService;
    }

    @Operation(summary = "Get site settings")
    @GetMapping
    public ApiResponse<SiteSettingVO> get() {
        return ApiResponse.ok(siteSettingService.get());
    }

    @Operation(summary = "Update site settings")
    @PutMapping
    public ApiResponse<Void> update(@Valid @RequestBody SiteSettingRequest req) {
        siteSettingService.update(req);
        return ApiResponse.ok(null);
    }
}
