package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.service.UploadService;
import com.example.blog.vo.UploadResultVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/admin/upload")
@Validated
public class AdminUploadController {

    private final UploadService uploadService;

    public AdminUploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/image")
    public ApiResponse<UploadResultVO> uploadImage(@RequestPart("file") @NotNull MultipartFile file) {
        return ApiResponse.ok(uploadService.uploadImage(file));
    }
}
