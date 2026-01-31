package com.example.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCodes;
import com.example.blog.common.PageResult;
import com.example.blog.common.TimeUtil;
import com.example.blog.dto.PostQuery;
import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.entity.PostTag;
import com.example.blog.entity.Tag;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.mapper.PostMapper;
import com.example.blog.mapper.PostTagMapper;
import com.example.blog.mapper.TagMapper;
import com.example.blog.vo.ArchiveMonthGroupVO;
import com.example.blog.vo.ArchivePostVO;
import com.example.blog.vo.CategoryVO;
import com.example.blog.vo.HotPostVO;
import com.example.blog.vo.PostDetailVO;
import com.example.blog.vo.PostListItemVO;
import com.example.blog.vo.TagVO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostMapper postMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final PostTagMapper postTagMapper;

    public PostService(PostMapper postMapper, CategoryMapper categoryMapper, TagMapper tagMapper, PostTagMapper postTagMapper) {
        this.postMapper = postMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.postTagMapper = postTagMapper;
    }

    public List<ArchiveMonthGroupVO> archives() {
        // load all published posts (simple implementation for v1.0)
        List<Post> posts = postMapper.selectList(
                Wrappers.<Post>lambdaQuery()
                        .eq(Post::getIsDeleted, 0)
                        .eq(Post::getStatus, "PUBLISHED")
                        .isNotNull(Post::getPublishedAt)
                        .orderByDesc(Post::getPublishedAt)
        );

        // group by yyyy-MM
        Map<String, List<Post>> groups = new LinkedHashMap<>();
        for (Post p : posts) {
            String month = TimeUtil.format(p.getPublishedAt());
            // defensive: format returns full timestamp, we only need yyyy-MM
            if (month != null && month.length() >= 7) {
                month = month.substring(0, 7);
            } else {
                month = "unknown";
            }
            groups.computeIfAbsent(month, k -> new ArrayList<>()).add(p);
        }

        List<ArchiveMonthGroupVO> res = new ArrayList<>();
        for (Map.Entry<String, List<Post>> e : groups.entrySet()) {
            ArchiveMonthGroupVO g = new ArchiveMonthGroupVO();
            g.setMonth(e.getKey());
            g.setCount(e.getValue().size());
            List<ArchivePostVO> ps = e.getValue().stream().map(p -> {
                ArchivePostVO vo = new ArchivePostVO();
                vo.setId(p.getId());
                vo.setTitle(p.getTitle());
                vo.setPublishedAt(TimeUtil.format(p.getPublishedAt()));
                return vo;
            }).collect(Collectors.toList());
            g.setPosts(ps);
            res.add(g);
        }
        return res;
    }

    public List<HotPostVO> hot(Integer limit) {
        int n = limit == null ? 10 : limit;
        if (n < 1) n = 1;
        if (n > 50) n = 50;

        Page<Post> page = postMapper.selectPage(new Page<>(1, n),
                Wrappers.<Post>lambdaQuery()
                        .eq(Post::getIsDeleted, 0)
                        .eq(Post::getStatus, "PUBLISHED")
                        .orderByDesc(Post::getViewCount)
                        .orderByDesc(Post::getPublishedAt)
        );

        return page.getRecords().stream().map(p -> {
            HotPostVO vo = new HotPostVO();
            vo.setId(p.getId());
            vo.setTitle(p.getTitle());
            vo.setViewCount(p.getViewCount() == null ? 0L : p.getViewCount());
            vo.setPublishedAt(TimeUtil.format(p.getPublishedAt()));
            return vo;
        }).collect(Collectors.toList());
    }

    public PageResult<PostListItemVO> listPublished(PostQuery q) {
        long pageNum = q.getPageNum() == null ? 1 : q.getPageNum();
        long pageSize = q.getPageSize() == null ? 10 : q.getPageSize();
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        LambdaQueryWrapper<Post> wrapper = Wrappers.<Post>lambdaQuery()
                .eq(Post::getIsDeleted, 0)
                .eq(Post::getStatus, "PUBLISHED")
                .orderByDesc(Post::getPublishedAt);

        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            wrapper.like(Post::getTitle, q.getKeyword().trim());
        }
        if (q.getCategoryId() != null) {
            wrapper.eq(Post::getCategoryId, q.getCategoryId());
        }

        // tagId filtering: join via post_tag first
        if (q.getTagId() != null) {
            List<PostTag> pts = postTagMapper.selectList(
                    Wrappers.<PostTag>lambdaQuery().eq(PostTag::getTagId, q.getTagId())
            );
            if (pts.isEmpty()) {
                return new PageResult<>(Collections.emptyList(), 0, pageNum, pageSize);
            }
            List<Long> postIds = pts.stream().map(PostTag::getPostId).distinct().collect(Collectors.toList());
            wrapper.in(Post::getId, postIds);
        }

        Page<Post> page = postMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<Post> posts = page.getRecords();

        Map<Long, Category> catMap = loadCategories(posts.stream().map(Post::getCategoryId).filter(Objects::nonNull).collect(Collectors.toSet()));
        Map<Long, List<Tag>> tagMap = loadTagsForPosts(posts.stream().map(Post::getId).collect(Collectors.toList()));

        List<PostListItemVO> list = posts.stream().map(p -> {
            PostListItemVO vo = new PostListItemVO();
            vo.setId(p.getId());
            vo.setTitle(p.getTitle());
            vo.setSummary(p.getSummary());
            vo.setCoverUrl(p.getCoverUrl());
            vo.setPublishedAt(TimeUtil.format(p.getPublishedAt()));
            vo.setViewCount(p.getViewCount() == null ? 0L : p.getViewCount());
            if (p.getCategoryId() != null) {
                Category c = catMap.get(p.getCategoryId());
                if (c != null) vo.setCategory(new CategoryVO(c.getId(), c.getName()));
            }
            List<Tag> tags = tagMap.getOrDefault(p.getId(), Collections.emptyList());
            vo.setTags(tags.stream().map(t -> new TagVO(t.getId(), t.getName())).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(list, page.getTotal(), pageNum, pageSize);
    }

    public PostDetailVO getPublishedDetail(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null || post.getIsDeleted() != null && post.getIsDeleted() == 1 || !"PUBLISHED".equals(post.getStatus())) {
            throw new BizException(ErrorCodes.NOT_FOUND, "post not found");
        }

        // atomic increment view_count
        postMapper.update(null,
                Wrappers.<Post>lambdaUpdate()
                        .setSql("view_count = view_count + 1")
                        .eq(Post::getId, id)
        );
        // reload to get latest count
        post = postMapper.selectById(id);

        PostDetailVO vo = new PostDetailVO();
        vo.setId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setSummary(post.getSummary());
        vo.setContent(post.getContent());
        vo.setCoverUrl(post.getCoverUrl());
        vo.setPublishedAt(TimeUtil.format(post.getPublishedAt()));
        vo.setViewCount(post.getViewCount() == null ? 0L : post.getViewCount());

        if (post.getCategoryId() != null) {
            Category cat = categoryMapper.selectById(post.getCategoryId());
            if (cat != null) vo.setCategory(new CategoryVO(cat.getId(), cat.getName()));
        }

        Map<Long, List<Tag>> tagMap = loadTagsForPosts(Collections.singletonList(post.getId()));
        List<Tag> tags = tagMap.getOrDefault(post.getId(), Collections.emptyList());
        vo.setTags(tags.stream().map(t -> new TagVO(t.getId(), t.getName())).collect(Collectors.toList()));

        return vo;
    }

    private Map<Long, Category> loadCategories(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyMap();
        List<Category> cats = categoryMapper.selectBatchIds(ids);
        Map<Long, Category> map = new HashMap<>();
        for (Category c : cats) map.put(c.getId(), c);
        return map;
    }

    private Map<Long, List<Tag>> loadTagsForPosts(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) return Collections.emptyMap();

        List<PostTag> pts = postTagMapper.selectList(
                Wrappers.<PostTag>lambdaQuery().in(PostTag::getPostId, postIds)
        );
        if (pts.isEmpty()) return Collections.emptyMap();

        Set<Long> tagIds = pts.stream().map(PostTag::getTagId).collect(Collectors.toSet());
        List<Tag> tags = tagMapper.selectBatchIds(tagIds);
        Map<Long, Tag> tagById = tags.stream().collect(Collectors.toMap(Tag::getId, t -> t));

        Map<Long, List<Tag>> res = new HashMap<>();
        for (PostTag pt : pts) {
            Tag t = tagById.get(pt.getTagId());
            if (t == null) continue;
            res.computeIfAbsent(pt.getPostId(), k -> new ArrayList<>()).add(t);
        }
        return res;
    }
}
