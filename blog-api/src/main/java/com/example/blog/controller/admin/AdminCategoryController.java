package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.admin.NameOnlyRequest;
import com.example.blog.service.AdminCategoryService;
import com.example.blog.vo.CategoryVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@Validated
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    public AdminCategoryController(AdminCategoryService adminCategoryService) {
        this.adminCategoryService = adminCategoryService;
    }

    @GetMapping
    public ApiResponse<List<CategoryVO>> list() {
        return ApiResponse.ok(adminCategoryService.list());
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody NameOnlyRequest req) {
        return ApiResponse.ok(adminCategoryService.create(req.getName()));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable("id") Long id, @Valid @RequestBody NameOnlyRequest req) {
        adminCategoryService.update(id, req.getName());
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        adminCategoryService.delete(id);
        return ApiResponse.ok(null);
    }
}
