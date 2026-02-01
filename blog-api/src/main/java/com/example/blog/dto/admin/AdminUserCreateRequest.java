package com.example.blog.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AdminUserCreateRequest {

    @Schema(description = "Login username", example = "admin2")
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Schema(description = "Initial password", example = "123456")
    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @Schema(description = "Display name (optional)", example = "Editor")
    @Size(max = 50)
    private String displayName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
