package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Comment")
public class CommentVO {

    @Schema(description = "Comment id", example = "1")
    private Long id;

    @Schema(description = "Nickname", example = "Tom")
    private String nickname;

    @Schema(description = "Content", example = "Nice post!")
    private String content;

    @Schema(description = "Created time", example = "2026-02-01 12:00:00")
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
