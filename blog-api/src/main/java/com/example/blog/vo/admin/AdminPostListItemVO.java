package com.example.blog.vo.admin;

import com.example.blog.vo.CategoryVO;

public class AdminPostListItemVO {
    private Long id;
    private String title;
    private String status;
    private CategoryVO category;
    private String publishedAt;
    private String updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public CategoryVO getCategory() { return category; }
    public void setCategory(CategoryVO category) { this.category = category; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
