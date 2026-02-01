package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Web - Categories", description = "Public categories")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Operation(summary = "List categories", description = "Returns all categories.")
    @GetMapping
    public ApiResponse<List<CategoryVO>> list() {
        return ApiResponse.ok(
                categoryMapper.selectList(null).stream()
                        .map(c -> new CategoryVO(c.getId(), c.getName()))
                        .collect(Collectors.toList())
        );
    }
}
