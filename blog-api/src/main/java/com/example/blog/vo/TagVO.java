package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tag")
public class TagVO {

    @Schema(description = "Tag id", example = "1")
    private Long id;

    @Schema(description = "Tag name", example = "Spring")
    private String name;

    public TagVO() {}

    public TagVO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
