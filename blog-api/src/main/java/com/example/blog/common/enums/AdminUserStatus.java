package com.example.blog.common.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Admin user status")
public enum AdminUserStatus {

    @Schema(description = "Enabled")
    ENABLED,

    @Schema(description = "Disabled")
    DISABLED
}
