package com.example.blog.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCodes;
import com.example.blog.common.TimeUtil;
import com.example.blog.dto.CommentCreateRequest;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.mapper.CommentMapper;
import com.example.blog.mapper.PostMapper;
import com.example.blog.vo.CommentVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    public CommentService(CommentMapper commentMapper, PostMapper postMapper) {
        this.commentMapper = commentMapper;
        this.postMapper = postMapper;
    }

    public List<CommentVO> listApprovedByPostId(Long postId) {
        List<Comment> list = commentMapper.selectList(
                Wrappers.<Comment>lambdaQuery()
                        .eq(Comment::getPostId, postId)
                        .eq(Comment::getStatus, "APPROVED")
                        .orderByAsc(Comment::getCreatedAt)
        );
        return list.stream().map(c -> {
            CommentVO vo = new CommentVO();
            vo.setId(c.getId());
            vo.setNickname(c.getNickname());
            vo.setContent(c.getContent());
            vo.setCreatedAt(TimeUtil.format(c.getCreatedAt()));
            return vo;
        }).collect(Collectors.toList());
    }

    public Long create(Long postId, CommentCreateRequest req, String ip) {
        Post post = postMapper.selectById(postId);
        if (post == null || (post.getIsDeleted() != null && post.getIsDeleted() == 1) || !"PUBLISHED".equals(post.getStatus())) {
            throw new BizException(ErrorCodes.NOT_FOUND, "post not found");
        }

        Comment c = new Comment();
        c.setPostId(postId);
        c.setNickname(req.getNickname().trim());
        c.setEmail(req.getEmail() == null ? null : req.getEmail().trim());
        c.setContent(req.getContent().trim());
        c.setStatus("PENDING");
        c.setIp(ip);
        c.setCreatedAt(LocalDateTime.now());

        commentMapper.insert(c);
        return c.getId();
    }
}
