package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.admin.NameOnlyRequest;
import com.example.blog.service.AdminCategoryService;
import com.example.blog.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Admin - Categories", description = "Category management")
@RestController
@RequestMapping("/api/admin/categories")
@Validated
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    public AdminCategoryController(AdminCategoryService adminCategoryService) {
        this.adminCategoryService = adminCategoryService;
    }

    @Operation(summary = "List categories")
    @GetMapping
    public ApiResponse<List<CategoryVO>> list() {
        return ApiResponse.ok(adminCategoryService.list());
    }

    @Operation(summary = "Create category")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody NameOnlyRequest req) {
        return ApiResponse.ok(adminCategoryService.create(req.getName()));
    }

    @Operation(summary = "Update category")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable("id") Long id, @Valid @RequestBody NameOnlyRequest req) {
        adminCategoryService.update(id, req.getName());
        return ApiResponse.ok(null);
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        adminCategoryService.delete(id);
        return ApiResponse.ok(null);
    }
}
