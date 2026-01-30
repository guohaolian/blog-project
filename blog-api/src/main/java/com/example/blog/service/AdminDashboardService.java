package com.example.blog.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.entity.Post;
import com.example.blog.mapper.PostMapper;
import com.example.blog.vo.admin.AdminDashboardStatsVO;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {

    private final PostMapper postMapper;

    public AdminDashboardService(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    public AdminDashboardStatsVO stats() {
        long total = postMapper.selectCount(Wrappers.<Post>lambdaQuery().eq(Post::getIsDeleted, 0));
        long draft = postMapper.selectCount(Wrappers.<Post>lambdaQuery().eq(Post::getIsDeleted, 0).eq(Post::getStatus, "DRAFT"));
        long published = postMapper.selectCount(Wrappers.<Post>lambdaQuery().eq(Post::getIsDeleted, 0).eq(Post::getStatus, "PUBLISHED"));
        return new AdminDashboardStatsVO(total, draft, published);
    }
}
