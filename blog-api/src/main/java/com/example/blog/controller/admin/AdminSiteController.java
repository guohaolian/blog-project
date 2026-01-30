package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.admin.SiteSettingRequest;
import com.example.blog.service.SiteSettingService;
import com.example.blog.vo.SiteSettingVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/site")
@Validated
public class AdminSiteController {

    private final SiteSettingService siteSettingService;

    public AdminSiteController(SiteSettingService siteSettingService) {
        this.siteSettingService = siteSettingService;
    }

    @GetMapping
    public ApiResponse<SiteSettingVO> get() {
        return ApiResponse.ok(siteSettingService.get());
    }

    @PutMapping
    public ApiResponse<Void> update(@Valid @RequestBody SiteSettingRequest req) {
        siteSettingService.update(req);
        return ApiResponse.ok(null);
    }
}
