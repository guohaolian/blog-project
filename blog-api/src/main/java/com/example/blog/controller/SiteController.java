package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.service.SiteSettingService;
import com.example.blog.vo.SiteSettingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Web - Site", description = "Public site settings")
@RestController
@RequestMapping("/api/site")
public class SiteController {

    private final SiteSettingService siteSettingService;

    public SiteController(SiteSettingService siteSettingService) {
        this.siteSettingService = siteSettingService;
    }

    @Operation(summary = "Get site settings", description = "Fetch site settings for frontend rendering (siteName/notice/about/SEO/footer/banner).")
    @GetMapping
    public ApiResponse<SiteSettingVO> get() {
        return ApiResponse.ok(siteSettingService.get());
    }
}
