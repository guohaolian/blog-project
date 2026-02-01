package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Post list item")
public class PostListItemVO {

    @Schema(description = "Post id", example = "1")
    private Long id;

    @Schema(description = "Post title", example = "Hello World")
    private String title;

    @Schema(description = "Post summary", example = "Short introduction")
    private String summary;

    @Schema(description = "Cover image URL", example = "/uploads/202602/cover.jpg")
    private String coverUrl;

    @Schema(description = "Category")
    private CategoryVO category;

    @Schema(description = "Tags")
    private List<TagVO> tags;

    @Schema(description = "Published time (string)", example = "2026-02-01 12:00:00")
    private String publishedAt;

    @Schema(description = "View count", example = "123")
    private Long viewCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public CategoryVO getCategory() { return category; }
    public void setCategory(CategoryVO category) { this.category = category; }

    public List<TagVO> getTags() { return tags; }
    public void setTags(List<TagVO> tags) { this.tags = tags; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
}
