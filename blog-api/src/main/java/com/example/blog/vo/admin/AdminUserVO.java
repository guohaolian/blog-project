package com.example.blog.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Admin user")
public class AdminUserVO {

    @Schema(description = "Admin id", example = "1")
    private Long id;

    @Schema(description = "Username", example = "admin")
    private String username;

    @Schema(description = "Display name", example = "Administrator")
    private String displayName;

    @Schema(description = "Status: 1=enabled, 0=disabled", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "Created time", example = "2026-02-01 12:00:00")
    private String createdAt;

    @Schema(description = "Updated time", example = "2026-02-01 12:00:00")
    private String updatedAt;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
