package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Hot post")
public class HotPostVO {

    @Schema(description = "Post id", example = "1")
    private Long id;

    @Schema(description = "Post title", example = "Hello World")
    private String title;

    @Schema(description = "View count", example = "123")
    private Long viewCount;

    @Schema(description = "Published time", example = "2026-02-01 12:00:00")
    private String publishedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}
