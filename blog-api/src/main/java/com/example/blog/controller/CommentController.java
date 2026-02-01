package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.CommentCreateRequest;
import com.example.blog.service.CommentService;
import com.example.blog.vo.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "Web - Comments", description = "Public comment APIs")
@RestController
@RequestMapping("/api/posts")
@Validated
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "List approved comments", description = "Lists comments for a post (APPROVED only).")
    @GetMapping("/{id}/comments")
    public ApiResponse<List<CommentVO>> list(@PathVariable("id") Long postId) {
        return ApiResponse.ok(commentService.listApprovedByPostId(postId));
    }

    @Operation(summary = "Create comment", description = "Creates a comment for a post. New comments default to PENDING for admin review.")
    @PostMapping("/{id}/comments")
    public ApiResponse<Long> create(@PathVariable("id") Long postId, @Valid @RequestBody CommentCreateRequest req, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return ApiResponse.ok(commentService.create(postId, req, ip));
    }
}
