package com.example.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCodes;
import com.example.blog.common.PageResult;
import com.example.blog.common.TimeUtil;
import com.example.blog.dto.admin.AdminPostCreateRequest;
import com.example.blog.dto.admin.AdminPostQuery;
import com.example.blog.dto.admin.AdminPostUpdateRequest;
import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.entity.PostTag;
import com.example.blog.entity.Tag;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.mapper.PostMapper;
import com.example.blog.mapper.PostTagMapper;
import com.example.blog.mapper.TagMapper;
import com.example.blog.vo.CategoryVO;
import com.example.blog.vo.TagVO;
import com.example.blog.vo.admin.AdminPostEditVO;
import com.example.blog.vo.admin.AdminPostListItemVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminPostService {

    private final PostMapper postMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final PostTagMapper postTagMapper;

    public AdminPostService(PostMapper postMapper, CategoryMapper categoryMapper, TagMapper tagMapper, PostTagMapper postTagMapper) {
        this.postMapper = postMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.postTagMapper = postTagMapper;
    }

    public PageResult<AdminPostListItemVO> page(AdminPostQuery q) {
        long pageNum = q.getPageNum() == null ? 1 : q.getPageNum();
        long pageSize = q.getPageSize() == null ? 10 : q.getPageSize();
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        LambdaQueryWrapper<Post> wrapper = Wrappers.<Post>lambdaQuery()
                .eq(Post::getIsDeleted, 0)
                .orderByDesc(Post::getUpdatedAt);

        if (q.getStatus() != null && !q.getStatus().isBlank()) {
            wrapper.eq(Post::getStatus, q.getStatus().trim());
        }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            wrapper.like(Post::getTitle, q.getKeyword().trim());
        }
        if (q.getCategoryId() != null) {
            wrapper.eq(Post::getCategoryId, q.getCategoryId());
        }
        // tagId filtering: do minimal by joining via post_tag first
        if (q.getTagId() != null) {
            List<PostTag> pts = postTagMapper.selectList(Wrappers.<PostTag>lambdaQuery().eq(PostTag::getTagId, q.getTagId()));
            if (pts.isEmpty()) {
                return new PageResult<>(Collections.emptyList(), 0, pageNum, pageSize);
            }
            List<Long> postIds = pts.stream().map(PostTag::getPostId).distinct().collect(Collectors.toList());
            wrapper.in(Post::getId, postIds);
        }

        Page<Post> page = postMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<Post> posts = page.getRecords();

        Map<Long, Category> catMap = new HashMap<>();
        Set<Long> catIds = posts.stream().map(Post::getCategoryId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (!catIds.isEmpty()) {
            for (Category c : categoryMapper.selectBatchIds(catIds)) catMap.put(c.getId(), c);
        }

        List<AdminPostListItemVO> list = posts.stream().map(p -> {
            AdminPostListItemVO vo = new AdminPostListItemVO();
            vo.setId(p.getId());
            vo.setTitle(p.getTitle());
            vo.setStatus(p.getStatus());
            vo.setPublishedAt(TimeUtil.format(p.getPublishedAt()));
            vo.setUpdatedAt(TimeUtil.format(p.getUpdatedAt()));
            if (p.getCategoryId() != null) {
                Category c = catMap.get(p.getCategoryId());
                if (c != null) vo.setCategory(new CategoryVO(c.getId(), c.getName()));
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(list, page.getTotal(), pageNum, pageSize);
    }

    public Long create(AdminPostCreateRequest req) {
        LocalDateTime now = LocalDateTime.now();

        Post post = new Post();
        post.setTitle(req.getTitle());
        post.setSummary(req.getSummary());
        post.setContent(req.getContent());
        post.setCoverUrl(req.getCoverUrl());
        post.setStatus("DRAFT");
        post.setCategoryId(req.getCategoryId());
        post.setViewCount(0L);
        post.setIsDeleted(0);
        post.setPublishedAt(null);
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        postMapper.insert(post);

        updateTags(post.getId(), req.getTagIds());

        return post.getId();
    }

    public AdminPostEditVO getEdit(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null || (post.getIsDeleted() != null && post.getIsDeleted() == 1)) {
            throw new BizException(ErrorCodes.NOT_FOUND, "post not found");
        }

        AdminPostEditVO vo = new AdminPostEditVO();
        vo.setId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setSummary(post.getSummary());
        vo.setContent(post.getContent());
        vo.setCoverUrl(post.getCoverUrl());
        vo.setCategoryId(post.getCategoryId());
        vo.setStatus(post.getStatus());

        List<PostTag> pts = postTagMapper.selectList(Wrappers.<PostTag>lambdaQuery().eq(PostTag::getPostId, id));
        vo.setTagIds(pts.stream().map(PostTag::getTagId).collect(Collectors.toList()));

        return vo;
    }

    public void update(Long id, AdminPostUpdateRequest req) {
        Post post = postMapper.selectById(id);
        if (post == null || (post.getIsDeleted() != null && post.getIsDeleted() == 1)) {
            throw new BizException(ErrorCodes.NOT_FOUND, "post not found");
        }

        post.setTitle(req.getTitle());
        post.setSummary(req.getSummary());
        post.setContent(req.getContent());
        post.setCoverUrl(req.getCoverUrl());
        post.setCategoryId(req.getCategoryId());
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);

        updateTags(id, req.getTagIds());
    }

    public void delete(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) return;
        post.setIsDeleted(1);
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
    }

    public void publish(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null || (post.getIsDeleted() != null && post.getIsDeleted() == 1)) {
            throw new BizException(ErrorCodes.NOT_FOUND, "post not found");
        }
        post.setStatus("PUBLISHED");
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
    }

    public void unpublish(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null || (post.getIsDeleted() != null && post.getIsDeleted() == 1)) {
            throw new BizException(ErrorCodes.NOT_FOUND, "post not found");
        }
        post.setStatus("DRAFT");
        post.setPublishedAt(null);
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
    }

    public List<CategoryVO> categories() {
        List<Category> list = categoryMapper.selectList(Wrappers.<Category>lambdaQuery().orderByAsc(Category::getId));
        return list.stream().map(c -> new CategoryVO(c.getId(), c.getName())).collect(Collectors.toList());
    }

    public List<TagVO> tags() {
        List<Tag> list = tagMapper.selectList(Wrappers.<Tag>lambdaQuery().orderByAsc(Tag::getId));
        return list.stream().map(t -> new TagVO(t.getId(), t.getName())).collect(Collectors.toList());
    }

    private void updateTags(Long postId, List<Long> tagIds) {
        // clear
        postTagMapper.delete(Wrappers.<PostTag>lambdaQuery().eq(PostTag::getPostId, postId));
        if (tagIds == null || tagIds.isEmpty()) return;

        List<Long> uniq = tagIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        for (Long tagId : uniq) {
            PostTag pt = new PostTag();
            pt.setPostId(postId);
            pt.setTagId(tagId);
            postTagMapper.insert(pt);
        }
    }
}
