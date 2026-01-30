package com.example.blog.dto.admin;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SiteSettingRequest {

    @NotBlank
    @Size(max = 100)
    private String siteName;

    @Size(max = 255)
    private String siteNotice;

    private String aboutContent;

    /** JSON string, e.g. [{"name":"GitHub","url":"https://..."}] */
    private String linksJson;

    @Size(max = 255)
    private String seoTitle;

    @Size(max = 255)
    private String seoKeywords;

    @Size(max = 255)
    private String seoDescription;

    @Size(max = 255)
    private String footerText;

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
}
