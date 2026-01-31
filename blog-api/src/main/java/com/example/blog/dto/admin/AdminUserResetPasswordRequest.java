package com.example.blog.dto.admin;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AdminUserResetPasswordRequest {

    @NotBlank
    @Size(min = 6, max = 100)
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
