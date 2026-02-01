package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.admin.NameOnlyRequest;
import com.example.blog.service.AdminTagService;
import com.example.blog.vo.TagVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Admin - Tags", description = "Tag management")
@RestController
@RequestMapping("/api/admin/tags")
@Validated
public class AdminTagController {

    private final AdminTagService adminTagService;

    public AdminTagController(AdminTagService adminTagService) {
        this.adminTagService = adminTagService;
    }

    @Operation(summary = "List tags")
    @GetMapping
    public ApiResponse<List<TagVO>> list() {
        return ApiResponse.ok(adminTagService.list());
    }

    @Operation(summary = "Create tag")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody NameOnlyRequest req) {
        return ApiResponse.ok(adminTagService.create(req.getName()));
    }

    @Operation(summary = "Update tag")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable("id") Long id, @Valid @RequestBody NameOnlyRequest req) {
        adminTagService.update(id, req.getName());
        return ApiResponse.ok(null);
    }

    @Operation(summary = "Delete tag")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        adminTagService.delete(id);
        return ApiResponse.ok(null);
    }
}
