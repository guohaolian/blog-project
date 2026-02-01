package com.example.blog.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SiteSettingRequest {

    @Schema(description = "Site name", example = "My Blog")
    @NotBlank
    @Size(max = 100)
    private String siteName;

    @Schema(description = "Site notice (displayed on home/detail pages)", example = "Welcome to my blog")
    @Size(max = 255)
    private String siteNotice;

    @Schema(description = "About page content in Markdown", example = "# About\n\nThis is my blog...")
    @Size(max = 20000)
    private String aboutContent;

    @Schema(description = "Friend links as JSON string, e.g. [{\"name\":\"GitHub\",\"url\":\"https://...\"}]", example = "[{\"name\":\"GitHub\",\"url\":\"https://github.com/xxx\"}]")
    @Size(max = 2000)
    private String linksJson;

    @Schema(description = "SEO title", example = "My Blog")
    @Size(max = 255)
    private String seoTitle;

    @Schema(description = "SEO keywords", example = "java,vue,spring")
    @Size(max = 255)
    private String seoKeywords;

    @Schema(description = "SEO description", example = "A personal tech blog")
    @Size(max = 255)
    private String seoDescription;

    @Schema(description = "Footer text", example = "Copyright © 2026")
    @Size(max = 255)
    private String footerText;

    @Schema(description = "Homepage full-screen banner image URL (under /uploads)", example = "/uploads/202602/banner.jpg")
    @Size(max = 255)
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
