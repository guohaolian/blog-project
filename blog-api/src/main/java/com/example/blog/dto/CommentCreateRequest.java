package com.example.blog.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CommentCreateRequest {

    @NotBlank
    @Size(max = 30)
    private String nickname;

    @Size(max = 100)
    private String email;

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
