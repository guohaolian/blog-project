package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.service.SiteSettingService;
import com.example.blog.vo.SiteSettingVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/site")
public class SiteController {

    private final SiteSettingService siteSettingService;

    public SiteController(SiteSettingService siteSettingService) {
        this.siteSettingService = siteSettingService;
    }

    @GetMapping
    public ApiResponse<SiteSettingVO> get() {
        return ApiResponse.ok(siteSettingService.get());
    }
}
