package com.example.blog.dto.admin;

public class AdminCommentQuery {

    private Long pageNum = 1L;
    private Long pageSize = 10L;

    private String status;
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
