package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.common.PageResult;
import com.example.blog.dto.admin.AdminPostCreateRequest;
import com.example.blog.dto.admin.AdminPostQuery;
import com.example.blog.dto.admin.AdminPostUpdateRequest;
import com.example.blog.service.AdminPostService;
import com.example.blog.vo.CategoryVO;
import com.example.blog.vo.TagVO;
import com.example.blog.vo.admin.AdminPostEditVO;
import com.example.blog.vo.admin.AdminPostListItemVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/posts")
@Validated
public class AdminPostController {

    private final AdminPostService adminPostService;

    public AdminPostController(AdminPostService adminPostService) {
        this.adminPostService = adminPostService;
    }

    @GetMapping
    public ApiResponse<PageResult<AdminPostListItemVO>> page(AdminPostQuery q) {
        return ApiResponse.ok(adminPostService.page(q));
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody AdminPostCreateRequest req) {
        return ApiResponse.ok(adminPostService.create(req));
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminPostEditVO> detail(@PathVariable("id") Long id) {
        return ApiResponse.ok(adminPostService.getEdit(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable("id") Long id, @Valid @RequestBody AdminPostUpdateRequest req) {
        adminPostService.update(id, req);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        adminPostService.delete(id);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}/publish")
    public ApiResponse<Void> publish(@PathVariable("id") Long id) {
        adminPostService.publish(id);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}/unpublish")
    public ApiResponse<Void> unpublish(@PathVariable("id") Long id) {
        adminPostService.unpublish(id);
        return ApiResponse.ok(null);
    }

    // helper endpoints for editor
    @GetMapping("/meta/categories")
    public ApiResponse<List<CategoryVO>> categories() {
        return ApiResponse.ok(adminPostService.categories());
    }

    @GetMapping("/meta/tags")
    public ApiResponse<List<TagVO>> tags() {
        return ApiResponse.ok(adminPostService.tags());
    }
}
