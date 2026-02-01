package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Archive month group")
public class ArchiveMonthGroupVO {

    @Schema(description = "Month string", example = "2026-02")
    private String month;

    @Schema(description = "Posts count in this month", example = "3")
    private Integer count;

    @Schema(description = "Posts")
    private List<ArchivePostVO> posts;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<ArchivePostVO> getPosts() {
        return posts;
    }

    public void setPosts(List<ArchivePostVO> posts) {
        this.posts = posts;
    }
}
