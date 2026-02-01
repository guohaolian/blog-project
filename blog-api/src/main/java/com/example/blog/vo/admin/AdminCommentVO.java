package com.example.blog.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Admin comment item")
public class AdminCommentVO {

    @Schema(description = "Comment id", example = "1")
    private Long id;

    @Schema(description = "Post id", example = "1")
    private Long postId;

    @Schema(description = "Post title", example = "Hello World")
    private String postTitle;

    @Schema(description = "Nickname", example = "Tom")
    private String nickname;

    @Schema(description = "Email", example = "tom@example.com")
    private String email;

    @Schema(description = "Content", example = "Nice post!")
    private String content;

    @Schema(description = "Status", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED"})
    private String status;

    @Schema(description = "Created time", example = "2026-02-01 12:00:00")
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getPostTitle() { return postTitle; }
    public void setPostTitle(String postTitle) { this.postTitle = postTitle; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
