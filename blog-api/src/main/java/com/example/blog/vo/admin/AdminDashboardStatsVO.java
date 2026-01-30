package com.example.blog.vo.admin;

public class AdminDashboardStatsVO {

    private long total;
    private long draft;
    private long published;

    public AdminDashboardStatsVO() {
    }

    public AdminDashboardStatsVO(long total, long draft, long published) {
        this.total = total;
        this.draft = draft;
        this.published = published;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getDraft() {
        return draft;
    }

    public void setDraft(long draft) {
        this.draft = draft;
    }

    public long getPublished() {
        return published;
    }

    public void setPublished(long published) {
        this.published = published;
    }
}
