package com.example.blog.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class AdminUserStatusRequest {

    @Schema(description = "Status: 1=enabled, 0=disabled", example = "1", allowableValues = {"0", "1"})
    @NotNull
    @Min(0)
    @Max(1)
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
