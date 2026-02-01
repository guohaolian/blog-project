package com.example.blog.vo.admin;

import com.example.blog.vo.CategoryVO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Admin post list item")
public class AdminPostListItemVO {

    @Schema(description = "Post id", example = "1")
    private Long id;

    @Schema(description = "Post title", example = "Hello World")
    private String title;

    @Schema(description = "Status", example = "PUBLISHED", allowableValues = {"DRAFT", "PUBLISHED"})
    private String status;

    @Schema(description = "Category")
    private CategoryVO category;

    @Schema(description = "Published time", example = "2026-02-01 12:00:00")
    private String publishedAt;

    @Schema(description = "Updated time", example = "2026-02-01 12:00:00")
    private String updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public CategoryVO getCategory() { return category; }
    public void setCategory(CategoryVO category) { this.category = category; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
