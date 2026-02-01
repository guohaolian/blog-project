package com.example.blog.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current admin profile")
public class AdminMeResponse {

    @Schema(description = "Admin id", example = "1")
    private Long id;

    @Schema(description = "Username", example = "admin")
    private String username;

    @Schema(description = "Display name", example = "Administrator")
    private String displayName;

    public AdminMeResponse() {}

    public AdminMeResponse(Long id, String username, String displayName) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
