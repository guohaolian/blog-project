package com.example.blog.vo.admin;

public class AdminDashboardStatsVO {

    private long total;
    private long draft;
    private long published;

    private long categories;
    private long tags;
    private long commentsPending;
    private long totalViews;

    public AdminDashboardStatsVO() {
    }

    public AdminDashboardStatsVO(long total, long draft, long published, long categories, long tags, long commentsPending, long totalViews) {
        this.total = total;
        this.draft = draft;
        this.published = published;
        this.categories = categories;
        this.tags = tags;
        this.commentsPending = commentsPending;
        this.totalViews = totalViews;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getDraft() { return draft; }
    public void setDraft(long draft) { this.draft = draft; }

    public long getPublished() { return published; }
    public void setPublished(long published) { this.published = published; }

    public long getCategories() { return categories; }
    public void setCategories(long categories) { this.categories = categories; }

    public long getTags() { return tags; }
    public void setTags(long tags) { this.tags = tags; }

    public long getCommentsPending() { return commentsPending; }
    public void setCommentsPending(long commentsPending) { this.commentsPending = commentsPending; }

    public long getTotalViews() { return totalViews; }
    public void setTotalViews(long totalViews) { this.totalViews = totalViews; }
}
