package com.example.blog.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Uploaded file resource")
public class FileResourceVO {

    @Schema(description = "Resource id", example = "1")
    private Long id;

    @Schema(description = "Public URL", example = "/uploads/202602/xxx.jpg")
    private String url;

    @Schema(description = "Original file name", example = "banner.jpg")
    private String originalName;

    @Schema(description = "File size in bytes", example = "12345")
    private Long size;

    @Schema(description = "Content type", example = "image/jpeg")
    private String contentType;

    @Schema(description = "Created time", example = "2026-02-01 12:00:00")
    private String createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
