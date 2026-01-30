package com.example.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCodes;
import com.example.blog.common.PageResult;
import com.example.blog.common.TimeUtil;
import com.example.blog.dto.admin.AdminCommentQuery;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.mapper.CommentMapper;
import com.example.blog.mapper.PostMapper;
import com.example.blog.vo.admin.AdminCommentVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminCommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    public AdminCommentService(CommentMapper commentMapper, PostMapper postMapper) {
        this.commentMapper = commentMapper;
        this.postMapper = postMapper;
    }

    public PageResult<AdminCommentVO> page(AdminCommentQuery q) {
        long pageNum = q.getPageNum() == null ? 1 : q.getPageNum();
        long pageSize = q.getPageSize() == null ? 10 : q.getPageSize();
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        LambdaQueryWrapper<Comment> wrapper = Wrappers.<Comment>lambdaQuery().orderByDesc(Comment::getCreatedAt);
        if (q.getStatus() != null && !q.getStatus().isBlank()) {
            wrapper.eq(Comment::getStatus, q.getStatus().trim());
        }
        if (q.getPostId() != null) {
            wrapper.eq(Comment::getPostId, q.getPostId());
        }

        Page<Comment> page = commentMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<Comment> records = page.getRecords();

        Set<Long> postIds = records.stream().map(Comment::getPostId).collect(Collectors.toSet());
        Map<Long, String> postTitleMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            List<Post> posts = postMapper.selectBatchIds(postIds);
            for (Post p : posts) {
                postTitleMap.put(p.getId(), p.getTitle());
            }
        }

        List<AdminCommentVO> list = records.stream().map(c -> {
            AdminCommentVO vo = new AdminCommentVO();
            vo.setId(c.getId());
            vo.setPostId(c.getPostId());
            vo.setPostTitle(postTitleMap.getOrDefault(c.getPostId(), ""));
            vo.setNickname(c.getNickname());
            vo.setEmail(c.getEmail());
            vo.setContent(c.getContent());
            vo.setStatus(c.getStatus());
            vo.setCreatedAt(TimeUtil.format(c.getCreatedAt()));
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(list, page.getTotal(), pageNum, pageSize);
    }

    public void approve(Long id) {
        updateStatus(id, "APPROVED");
    }

    public void reject(Long id) {
        updateStatus(id, "REJECTED");
    }

    public void delete(Long id) {
        commentMapper.deleteById(id);
    }

    private void updateStatus(Long id, String status) {
        Comment c = commentMapper.selectById(id);
        if (c == null) {
            throw new BizException(ErrorCodes.NOT_FOUND, "comment not found");
        }
        c.setStatus(status);
        commentMapper.updateById(c);
    }
}
