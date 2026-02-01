package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Category")
public class CategoryVO {

    @Schema(description = "Category id", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Java")
    private String name;

    public CategoryVO() {}

    public CategoryVO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
