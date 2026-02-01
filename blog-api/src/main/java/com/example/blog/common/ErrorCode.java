package com.example.blog.common;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Unified error code table.
 *
 * <p>Rule:
 * <ul>
 *   <li>0: success</li>
 *   <li>4xxxx: client errors (validation/auth/permission/not found)</li>
 *   <li>5xxxx: server errors</li>
 * </ul>
 */
@Schema(description = "Unified error code")
public enum ErrorCode {

    // --- success
    OK(0, "ok"),

    // --- common client errors
    BAD_REQUEST(40001, "bad request"),
    UNAUTHORIZED(40100, "unauthorized"),
    FORBIDDEN(40300, "forbidden"),
    NOT_FOUND(40400, "not found"),

    // --- common server errors
    SYSTEM_ERROR(50000, "system error"),

    // --- business/domain errors (extend as needed)
    ADMIN_USERNAME_OR_PASSWORD_INVALID(41001, "username or password invalid"),
    ADMIN_USER_DISABLED(41002, "admin user disabled"),

    POST_NOT_FOUND(42001, "post not found"),
    POST_NOT_PUBLISHED(42002, "post not published"),

    COMMENT_NOT_FOUND(43001, "comment not found"),
    COMMENT_STATUS_INVALID(43002, "comment status invalid");

    private final int code;
    private final String defaultMessage;

    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Schema(description = "Numeric code", example = "40001")
    public int getCode() {
        return code;
    }

    @Schema(description = "Default message", example = "bad request")
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
