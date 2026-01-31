package com.example.blog.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> handleBiz(BizException e) {
        return ApiResponse.fail(e.getCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class
    })
    public ApiResponse<Void> handleBadRequest(Exception e) {
        String msg = e.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = "bad request";
        }
        return ApiResponse.fail(ErrorCodes.BAD_REQUEST, msg);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<Void> handleAuth(AuthenticationException e) {
        return ApiResponse.fail(ErrorCodes.UNAUTHORIZED, "unauthorized");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> handleDenied(AccessDeniedException e) {
        return ApiResponse.fail(ErrorCodes.FORBIDDEN, "forbidden");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleAny(Exception e) {
        // Always log stacktrace on server side for debugging
        log.error("Unhandled exception", e);

        String msg = e.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = e.getClass().getSimpleName();
        }
        return ApiResponse.fail(ErrorCodes.SYSTEM_ERROR, msg);
    }
}
