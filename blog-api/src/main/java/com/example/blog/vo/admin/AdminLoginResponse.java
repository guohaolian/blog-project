package com.example.blog.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Admin login response")
public class AdminLoginResponse {

    @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    public AdminLoginResponse() {}

    public AdminLoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
