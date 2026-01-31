package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.common.PageResult;
import com.example.blog.dto.PostQuery;
import com.example.blog.service.PostService;
import com.example.blog.vo.ArchiveMonthGroupVO;
import com.example.blog.vo.HotPostVO;
import com.example.blog.vo.PostDetailVO;
import com.example.blog.vo.PostListItemVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ApiResponse<PageResult<PostListItemVO>> list(PostQuery q) {
        return ApiResponse.ok(postService.listPublished(q));
    }

    @GetMapping("/{id}")
    public ApiResponse<PostDetailVO> detail(@PathVariable("id") Long id) {
        return ApiResponse.ok(postService.getPublishedDetail(id));
    }

    @GetMapping("/hot")
    public ApiResponse<List<HotPostVO>> hot(@RequestParam(value = "limit", required = false) Integer limit) {
        return ApiResponse.ok(postService.hot(limit));
    }

    @GetMapping("/archives")
    public ApiResponse<List<ArchiveMonthGroupVO>> archives() {
        return ApiResponse.ok(postService.archives());
    }
}
