package com.example.blog.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class AdminCommentQuery {

    @Schema(description = "Page number (start from 1)", example = "1")
    @Min(1)
    private Long pageNum = 1L;

    @Schema(description = "Page size (1~100)", example = "10")
    @Min(1)
    @Max(100)
    private Long pageSize = 10L;

    @Schema(description = "Comment status (optional): PENDING/APPROVED/REJECTED", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED"})
    @Size(max = 20)
    private String status;

    @Schema(description = "Filter by postId", example = "1")
    private Long postId;

    public Long getPageNum() { return pageNum; }
    public void setPageNum(Long pageNum) { this.pageNum = pageNum; }

    public Long getPageSize() { return pageSize; }
    public void setPageSize(Long pageSize) { this.pageSize = pageSize; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
}
