package com.example.blog.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AdminLoginRequest {

    @Schema(description = "Admin username", example = "admin")
    @NotBlank
    @Size(max = 50)
    private String username;

    @Schema(description = "Admin password (plain text, will be verified server-side)", example = "123456")
    @NotBlank
    @Size(max = 100)
    private String password;

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
}
