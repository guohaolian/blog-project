package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.common.PageResult;
import com.example.blog.dto.admin.AdminCommentQuery;
import com.example.blog.service.AdminCommentService;
import com.example.blog.vo.admin.AdminCommentVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/comments")
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    public AdminCommentController(AdminCommentService adminCommentService) {
        this.adminCommentService = adminCommentService;
    }

    @GetMapping
    public ApiResponse<PageResult<AdminCommentVO>> page(AdminCommentQuery q) {
        return ApiResponse.ok(adminCommentService.page(q));
    }

    @PutMapping("/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable("id") Long id) {
        adminCommentService.approve(id);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable("id") Long id) {
        adminCommentService.reject(id);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        adminCommentService.delete(id);
        return ApiResponse.ok(null);
    }
}
