package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Archive post item")
public class ArchivePostVO {

    @Schema(description = "Post id", example = "1")
    private Long id;

    @Schema(description = "Post title", example = "Hello World")
    private String title;

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

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}
