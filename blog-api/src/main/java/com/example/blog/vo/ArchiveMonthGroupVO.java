package com.example.blog.vo;

import java.util.List;

public class ArchiveMonthGroupVO {
    private String month;
    private Integer count;
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
