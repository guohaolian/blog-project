package com.example.blog.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dashboard stats")
public class AdminDashboardStatsVO {

    @Schema(description = "Total posts", example = "10")
    private long total;

    @Schema(description = "Draft posts", example = "2")
    private long draft;

    @Schema(description = "Published posts", example = "8")
    private long published;

    @Schema(description = "Categories count", example = "5")
    private long categories;

    @Schema(description = "Tags count", example = "12")
    private long tags;

    @Schema(description = "Pending comments count", example = "3")
    private long commentsPending;

    @Schema(description = "Total views of all posts", example = "12345")
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
