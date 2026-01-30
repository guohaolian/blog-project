package com.example.blog.vo;

import java.util.List;

public class PostListItemVO {
    private Long id;
    private String title;
    private String summary;
    private String coverUrl;
    private CategoryVO category;
    private List<TagVO> tags;
    private String publishedAt;
    private Long viewCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public CategoryVO getCategory() { return category; }
    public void setCategory(CategoryVO category) { this.category = category; }

    public List<TagVO> getTags() { return tags; }
    public void setTags(List<TagVO> tags) { this.tags = tags; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
}
