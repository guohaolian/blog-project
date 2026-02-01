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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Admin - Posts", description = "Post management")
@RestController
@RequestMapping("/api/admin/posts")
@Validated
public class AdminPostController {

    private final AdminPostService adminPostService;

    public AdminPostController(AdminPostService adminPostService) {
        this.adminPostService = adminPostService;
    }

    @Operation(summary = "List posts", description = "Admin post list with pagination and filters.")
    @GetMapping
    public ApiResponse<PageResult<AdminPostListItemVO>> page(AdminPostQuery q) {
        return ApiResponse.ok(adminPostService.page(q));
    }

    @Operation(summary = "Create post")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody AdminPostCreateRequest req) {
        return ApiResponse.ok(adminPostService.create(req));
    }

    @Operation(summary = "Get post detail for editing")
    @GetMapping("/{id}")
    public ApiResponse<AdminPostEditVO> detail(@PathVariable("id") Long id) {
        return ApiResponse.ok(adminPostService.getEdit(id));
    }

    @Operation(summary = "Update post")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable("id") Long id, @Valid @RequestBody AdminPostUpdateRequest req) {
        adminPostService.update(id, req);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "Delete post")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        adminPostService.delete(id);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "Publish post")
    @PutMapping("/{id}/publish")
    public ApiResponse<Void> publish(@PathVariable("id") Long id) {
        adminPostService.publish(id);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "Unpublish post")
    @PutMapping("/{id}/unpublish")
    public ApiResponse<Void> unpublish(@PathVariable("id") Long id) {
        adminPostService.unpublish(id);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "List categories for editor")
    @GetMapping("/meta/categories")
    public ApiResponse<List<CategoryVO>> categories() {
        return ApiResponse.ok(adminPostService.categories());
    }

    @Operation(summary = "List tags for editor")
    @GetMapping("/meta/tags")
    public ApiResponse<List<TagVO>> tags() {
        return ApiResponse.ok(adminPostService.tags());
    }
}
