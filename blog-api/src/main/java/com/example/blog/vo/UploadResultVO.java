package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Upload result")
public class UploadResultVO {

    @Schema(description = "Public URL", example = "/uploads/202602/xxx.jpg")
    private String url;

    @Schema(description = "Original file name", example = "banner.jpg")
    private String originalName;

    @Schema(description = "File size in bytes", example = "12345")
    private Long size;

    @Schema(description = "Content type", example = "image/jpeg")
    private String contentType;

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
}
