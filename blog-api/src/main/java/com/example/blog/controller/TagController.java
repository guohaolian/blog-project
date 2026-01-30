package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.mapper.TagMapper;
import com.example.blog.vo.TagVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagMapper tagMapper;

    public TagController(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @GetMapping
    public ApiResponse<List<TagVO>> list() {
        return ApiResponse.ok(
                tagMapper.selectList(null).stream()
                        .map(t -> new TagVO(t.getId(), t.getName()))
                        .collect(Collectors.toList())
        );
    }
}
