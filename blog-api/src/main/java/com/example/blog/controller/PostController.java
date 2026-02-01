package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.common.PageResult;
import com.example.blog.dto.PostQuery;
import com.example.blog.service.PostService;
import com.example.blog.vo.ArchiveMonthGroupVO;
import com.example.blog.vo.HotPostVO;
import com.example.blog.vo.PostDetailVO;
import com.example.blog.vo.PostListItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Web - Posts", description = "Public post browsing APIs")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "List published posts", description = "Supports pagination and optional filters (keyword/categoryId/tagId).")
    @GetMapping
    public ApiResponse<PageResult<PostListItemVO>> list(PostQuery q) {
        return ApiResponse.ok(postService.listPublished(q));
    }

    @Operation(summary = "Get published post detail", description = "Returns post detail and increases viewCount by 1.")
    @GetMapping("/{id}")
    public ApiResponse<PostDetailVO> detail(@PathVariable("id") Long id) {
        return ApiResponse.ok(postService.getPublishedDetail(id));
    }

    @Operation(summary = "Get hot posts", description = "Returns latest hot posts ordered by view count.")
    @GetMapping("/hot")
    public ApiResponse<List<HotPostVO>> hot(@RequestParam(value = "limit", required = false) Integer limit) {
        return ApiResponse.ok(postService.hot(limit));
    }

    @Operation(summary = "Get archives", description = "Returns archive groups aggregated by month.")
    @GetMapping("/archives")
    public ApiResponse<List<ArchiveMonthGroupVO>> archives() {
        return ApiResponse.ok(postService.archives());
    }
}
