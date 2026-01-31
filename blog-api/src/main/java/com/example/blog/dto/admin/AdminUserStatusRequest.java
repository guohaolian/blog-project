package com.example.blog.dto.admin;

import javax.validation.constraints.NotNull;

public class AdminUserStatusRequest {

    @NotNull
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
