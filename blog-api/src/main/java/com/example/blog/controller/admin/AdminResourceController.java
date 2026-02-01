package com.example.blog.controller.admin;

import com.example.blog.common.ApiResponse;
import com.example.blog.common.PageResult;
import com.example.blog.dto.admin.AdminResourceQuery;
import com.example.blog.service.AdminResourceService;
import com.example.blog.vo.admin.FileResourceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Admin - Resources", description = "Uploaded file resources")
@RestController
@RequestMapping("/api/admin/resources")
@Validated
public class AdminResourceController {

    private final AdminResourceService adminResourceService;

    public AdminResourceController(AdminResourceService adminResourceService) {
        this.adminResourceService = adminResourceService;
    }

    @Operation(summary = "List resources", description = "List uploaded resources with pagination and optional filters.")
    @GetMapping
    public ApiResponse<PageResult<FileResourceVO>> page(@Validated AdminResourceQuery q) {
        return ApiResponse.ok(adminResourceService.page(q.getPageNum(), q.getPageSize(), q.getKeyword(), q.getContentTypePrefix()));
    }

    @Operation(summary = "Delete resource", description = "Deletes resource record and removes file from disk if possible.")
    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Object>> delete(@PathVariable("id") Long id) {
        adminResourceService.delete(id);
        return ApiResponse.ok(Map.of("deleted", true));
    }
}
