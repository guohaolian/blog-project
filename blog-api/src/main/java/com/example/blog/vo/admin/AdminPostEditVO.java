package com.example.blog.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Admin post edit model")
public class AdminPostEditVO {

    @Schema(description = "Post id", example = "1")
    private Long id;

    @Schema(description = "Title", example = "Hello World")
    private String title;

    @Schema(description = "Summary", example = "Short introduction")
    private String summary;

    @Schema(description = "Content in Markdown", example = "# Heading\n\nContent...")
    private String content;

    @Schema(description = "Cover image URL", example = "/uploads/202602/cover.jpg")
    private String coverUrl;

    @Schema(description = "Category id", example = "1")
    private Long categoryId;

    @Schema(description = "Tag ids", example = "[1,2,3]")
    private List<Long> tagIds;

    @Schema(description = "Status", example = "DRAFT", allowableValues = {"DRAFT", "PUBLISHED"})
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
