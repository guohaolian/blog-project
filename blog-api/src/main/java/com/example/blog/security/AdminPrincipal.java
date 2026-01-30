package com.example.blog.security;

import java.security.Principal;

public class AdminPrincipal implements Principal {

    private final Long adminId;
    private final String username;

    public AdminPrincipal(Long adminId, String username) {
        this.adminId = adminId;
        this.username = username;
    }

    public Long getAdminId() {
        return adminId;
    }

    @Override
    public String getName() {
        return username;
    }
}
