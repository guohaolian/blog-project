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
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> handleBiz(BizException e, HttpServletResponse response) {
        response.setStatus(mapBizStatus(e).value());
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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResponse<Void> handleNoHandler(NoHandlerFoundException e) {
        return ApiResponse.fail(ErrorCode.NOT_FOUND.getCode(), ErrorCode.NOT_FOUND.getDefaultMessage());
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

    private HttpStatus mapBizStatus(BizException e) {
        int code = e.getCode();

        // --- auth / permission
        if (code == ErrorCode.UNAUTHORIZED.getCode()) return HttpStatus.UNAUTHORIZED;
        if (code == ErrorCode.FORBIDDEN.getCode()) return HttpStatus.FORBIDDEN;
        if (code == ErrorCode.ADMIN_USER_DISABLED.getCode()) return HttpStatus.FORBIDDEN;

        // --- not found
        if (code == ErrorCode.NOT_FOUND.getCode()) return HttpStatus.NOT_FOUND;
        if (code == ErrorCode.POST_NOT_FOUND.getCode()) return HttpStatus.NOT_FOUND;
        if (code == ErrorCode.COMMENT_NOT_FOUND.getCode()) return HttpStatus.NOT_FOUND;

        // --- default: treat other business/validation errors as 400
        return HttpStatus.BAD_REQUEST;
    }
}
