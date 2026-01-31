package com.example.blog.dto.admin;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class AdminResourceQuery {

    @Min(1)
    private long pageNum = 1;

    @Min(1)
    @Max(100)
    private long pageSize = 10;

    // optional fuzzy keyword for url / originalName
    private String keyword;

    // optional prefix filter: e.g. "image/"
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
