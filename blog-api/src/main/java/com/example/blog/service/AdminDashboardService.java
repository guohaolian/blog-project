package com.example.blog.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.entity.Category;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.Tag;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.mapper.CommentMapper;
import com.example.blog.mapper.PostMapper;
import com.example.blog.mapper.TagMapper;
import com.example.blog.vo.admin.AdminDashboardStatsVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDashboardService {

    private final PostMapper postMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final CommentMapper commentMapper;

    public AdminDashboardService(PostMapper postMapper, CategoryMapper categoryMapper, TagMapper tagMapper, CommentMapper commentMapper) {
        this.postMapper = postMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.commentMapper = commentMapper;
    }

    public AdminDashboardStatsVO stats() {
        long total = postMapper.selectCount(Wrappers.<Post>lambdaQuery().eq(Post::getIsDeleted, 0));
        long draft = postMapper.selectCount(Wrappers.<Post>lambdaQuery().eq(Post::getIsDeleted, 0).eq(Post::getStatus, "DRAFT"));
        long published = postMapper.selectCount(Wrappers.<Post>lambdaQuery().eq(Post::getIsDeleted, 0).eq(Post::getStatus, "PUBLISHED"));

        long categories = categoryMapper.selectCount(Wrappers.<Category>lambdaQuery());
        long tags = tagMapper.selectCount(Wrappers.<Tag>lambdaQuery());
        long commentsPending = commentMapper.selectCount(Wrappers.<Comment>lambdaQuery().eq(Comment::getStatus, "PENDING"));

        // totalViews: sum view_count of published posts (simple Java sum for MySQL 5.5 / avoid custom SQL)
        List<Post> publishedPosts = postMapper.selectList(Wrappers.<Post>lambdaQuery()
                .select(Post::getViewCount)
                .eq(Post::getIsDeleted, 0)
                .eq(Post::getStatus, "PUBLISHED"));
        long totalViews = 0;
        for (Post p : publishedPosts) {
            if (p.getViewCount() != null) totalViews += p.getViewCount();
        }

        return new AdminDashboardStatsVO(total, draft, published, categories, tags, commentsPending, totalViews);
    }
}
