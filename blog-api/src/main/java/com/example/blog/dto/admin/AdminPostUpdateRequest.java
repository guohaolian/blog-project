package com.example.blog.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class AdminPostUpdateRequest {

    @Schema(description = "Post title", example = "Hello World")
    @NotBlank
    @Size(max = 120)
    private String title;

    @Schema(description = "Post summary", example = "A short introduction about this post")
    @Size(max = 300)
    private String summary;

    @Schema(description = "Post content in Markdown", example = "# Heading\n\nContent...")
    @NotBlank
    private String content;

    @Schema(description = "Cover image URL (usually under /uploads)", example = "/uploads/202602/cover.jpg")
    @Size(max = 255)
    private String coverUrl;

    @Schema(description = "Category id", example = "1")
    private Long categoryId;

    @Schema(description = "Tag ids", example = "[1,2,3]")
    private List<Long> tagIds;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public List<Long> getTagIds() { return tagIds; }
    public void setTagIds(List<Long> tagIds) { this.tagIds = tagIds; }
}
