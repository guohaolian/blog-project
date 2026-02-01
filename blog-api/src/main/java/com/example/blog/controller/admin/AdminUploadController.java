package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.service.UploadService;
import com.example.blog.vo.UploadResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Tag(name = "Admin - Upload", description = "Upload APIs")
@RestController
@RequestMapping("/api/admin/upload")
@Validated
public class AdminUploadController {

    private final UploadService uploadService;

    public AdminUploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @Operation(summary = "Upload image", description = "Upload an image file and get a public /uploads/** URL.")
    @PostMapping("/image")
    public ApiResponse<UploadResultVO> uploadImage(@RequestPart("file") @NotNull MultipartFile file) {
        return ApiResponse.ok(uploadService.uploadImage(file));
    }
}
