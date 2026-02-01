package com.example.blog.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class AdminPostQuery {

    @Schema(description = "Page number (start from 1)", example = "1")
    @Min(1)
    private Long pageNum = 1L;

    @Schema(description = "Page size (1~100)", example = "10")
    @Min(1)
    @Max(100)
    private Long pageSize = 10L;

    @Schema(description = "Post status (optional): DRAFT/PUBLISHED", example = "PUBLISHED", allowableValues = {"DRAFT", "PUBLISHED"})
    @Size(max = 20)
    private String status;

    @Schema(description = "Keyword for searching title", example = "spring")
    @Size(max = 50)
    private String keyword;

    @Schema(description = "Filter by categoryId", example = "1")
    private Long categoryId;

    @Schema(description = "Filter by tagId", example = "2")
    private Long tagId;

    public Long getPageNum() { return pageNum; }
    public void setPageNum(Long pageNum) { this.pageNum = pageNum; }

    public Long getPageSize() { return pageSize; }
    public void setPageSize(Long pageSize) { this.pageSize = pageSize; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Long getTagId() { return tagId; }
    public void setTagId(Long tagId) { this.tagId = tagId; }
}
