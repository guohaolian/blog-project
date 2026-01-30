package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.CommentCreateRequest;
import com.example.blog.service.CommentService;
import com.example.blog.vo.CommentVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Validated
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{id}/comments")
    public ApiResponse<List<CommentVO>> list(@PathVariable("id") Long postId) {
        return ApiResponse.ok(commentService.listApprovedByPostId(postId));
    }

    @PostMapping("/{id}/comments")
    public ApiResponse<Long> create(@PathVariable("id") Long postId, @Valid @RequestBody CommentCreateRequest req, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return ApiResponse.ok(commentService.create(postId, req, ip));
    }
}
