package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "站点设置")
public class SiteSettingVO {

    @Schema(description = "站点名称", example = "我的博客")
    private String siteName;

    @Schema(description = "站点公告", example = "欢迎光临")
    private String siteNotice;

    @Schema(description = "关于内容（Markdown 格式）", example = "# 关于\n\n...")
    private String aboutContent;

    @Schema(description = "链接 JSON 字符串", example = "[{\"name\":\"GitHub\",\"url\":\"https://github.com/xxx\"}]")
    private String linksJson;

    @Schema(description = "SEO 标题", example = "我的博客")
    private String seoTitle;

    @Schema(description = "SEO 关键字", example = "java,vue,spring")
    private String seoKeywords;

    @Schema(description = "SEO 描述", example = "一个个人技术博客")
    private String seoDescription;

    @Schema(description = "页脚文本", example = "版权所有 © 2026")
    private String footerText;

    /** 首页全屏横幅图片 URL */
    @Schema(description = "首页全屏横幅图片 URL", example = "/uploads/202602/banner.jpg")
    private String bannerUrl;

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    public String getSiteNotice() { return siteNotice; }
    public void setSiteNotice(String siteNotice) { this.siteNotice = siteNotice; }

    public String getAboutContent() { return aboutContent; }
    public void setAboutContent(String aboutContent) { this.aboutContent = aboutContent; }

    public String getLinksJson() { return linksJson; }
    public void setLinksJson(String linksJson) { this.linksJson = linksJson; }

    public String getSeoTitle() { return seoTitle; }
    public void setSeoTitle(String seoTitle) { this.seoTitle = seoTitle; }

    public String getSeoKeywords() { return seoKeywords; }
    public void setSeoKeywords(String seoKeywords) { this.seoKeywords = seoKeywords; }

    public String getSeoDescription() { return seoDescription; }
    public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }

    public String getFooterText() { return footerText; }
    public void setFooterText(String footerText) { this.footerText = footerText; }

    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
}
