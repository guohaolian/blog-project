package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CommentCreateRequest {

    @Schema(description = "Nickname displayed on comment", example = "Tom")
    @NotBlank
    @Size(max = 30)
    private String nickname;

    @Schema(description = "Email (optional). Used for contact only, not displayed publicly", example = "tom@example.com")
    @Email
    @Size(max = 100)
    private String email;

    @Schema(description = "Comment content", example = "Nice post!")
    @NotBlank
    @Size(max = 500)
    private String content;

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
