package com.example.blog.common;

public final class ErrorCodes {
    private ErrorCodes() {}

    public static final int OK = 0;

    public static final int BAD_REQUEST = 40001;
    public static final int UNAUTHORIZED = 40100;
    public static final int FORBIDDEN = 40300;
    public static final int NOT_FOUND = 40400;

    public static final int SYSTEM_ERROR = 50000;
}
