package com.example.blog.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class AdminResourceQuery {

    @Schema(description = "Page number (start from 1)", example = "1")
    @Min(1)
    private long pageNum = 1;

    @Schema(description = "Page size (1~100)", example = "10")
    @Min(1)
    @Max(100)
    private long pageSize = 10;

    @Schema(description = "Optional keyword for fuzzy search (url/originalName)", example = "banner")
    @Size(max = 100)
    private String keyword;

    @Schema(description = "Optional content-type prefix filter, e.g. image/", example = "image/")
    @Size(max = 50)
    private String contentTypePrefix;

    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getContentTypePrefix() {
        return contentTypePrefix;
    }

    public void setContentTypePrefix(String contentTypePrefix) {
        this.contentTypePrefix = contentTypePrefix;
    }
}
