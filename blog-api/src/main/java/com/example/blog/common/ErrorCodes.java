package com.example.blog.common;

/**
 * @deprecated Prefer {@link ErrorCode}.
 * Kept for backward compatibility with existing code.
 */
@Deprecated
public final class ErrorCodes {
    private ErrorCodes() {}

    public static final int OK = ErrorCode.OK.getCode();

    public static final int BAD_REQUEST = ErrorCode.BAD_REQUEST.getCode();
    public static final int UNAUTHORIZED = ErrorCode.UNAUTHORIZED.getCode();
    public static final int FORBIDDEN = ErrorCode.FORBIDDEN.getCode();
    public static final int NOT_FOUND = ErrorCode.NOT_FOUND.getCode();

    public static final int SYSTEM_ERROR = ErrorCode.SYSTEM_ERROR.getCode();
}
