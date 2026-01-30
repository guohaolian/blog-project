package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.admin.NameOnlyRequest;
import com.example.blog.service.AdminTagService;
import com.example.blog.vo.TagVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/tags")
@Validated
public class AdminTagController {

    private final AdminTagService adminTagService;

    public AdminTagController(AdminTagService adminTagService) {
        this.adminTagService = adminTagService;
    }

    @GetMapping
    public ApiResponse<List<TagVO>> list() {
        return ApiResponse.ok(adminTagService.list());
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody NameOnlyRequest req) {
        return ApiResponse.ok(adminTagService.create(req.getName()));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable("id") Long id, @Valid @RequestBody NameOnlyRequest req) {
        adminTagService.update(id, req.getName());
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        adminTagService.delete(id);
        return ApiResponse.ok(null);
    }
}
