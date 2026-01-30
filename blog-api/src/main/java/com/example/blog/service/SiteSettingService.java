package com.example.blog.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.dto.admin.SiteSettingRequest;
import com.example.blog.entity.SiteSetting;
import com.example.blog.mapper.SiteSettingMapper;
import com.example.blog.vo.SiteSettingVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SiteSettingService {

    private static final long DEFAULT_ID = 1L;

    private final SiteSettingMapper siteSettingMapper;

    public SiteSettingService(SiteSettingMapper siteSettingMapper) {
        this.siteSettingMapper = siteSettingMapper;
    }

    public SiteSettingVO get() {
        SiteSetting s = siteSettingMapper.selectById(DEFAULT_ID);
        if (s == null) {
            s = ensureDefaultRow();
        }
        return toVO(s);
    }

    public void update(SiteSettingRequest req) {
        SiteSetting s = siteSettingMapper.selectById(DEFAULT_ID);
        if (s == null) {
            s = ensureDefaultRow();
        }

        s.setSiteName(req.getSiteName());
        s.setSiteNotice(req.getSiteNotice());
        s.setAboutContent(req.getAboutContent());
        s.setLinksJson(req.getLinksJson());
        s.setSeoTitle(req.getSeoTitle());
        s.setSeoKeywords(req.getSeoKeywords());
        s.setSeoDescription(req.getSeoDescription());
        s.setFooterText(req.getFooterText());
        s.setUpdatedAt(LocalDateTime.now());

        siteSettingMapper.updateById(s);
    }

    private SiteSetting ensureDefaultRow() {
        SiteSetting s = new SiteSetting();
        s.setId(DEFAULT_ID);
        s.setSiteName("My Blog");
        s.setSiteNotice("Welcome");
        s.setAboutContent("# About\n\nAbout page.");
        s.setLinksJson("[]");
        s.setSeoTitle("My Blog");
        s.setSeoKeywords("blog,java,vue");
        s.setSeoDescription("A simple blog site");
        s.setFooterText("(c) My Blog");
        s.setUpdatedAt(LocalDateTime.now());

        // if row already inserted concurrently, ignore
        try {
            siteSettingMapper.insert(s);
        } catch (Exception ignore) {
            // fall back to select
            return siteSettingMapper.selectById(DEFAULT_ID);
        }
        return s;
    }

    private SiteSettingVO toVO(SiteSetting s) {
        SiteSettingVO vo = new SiteSettingVO();
        vo.setSiteName(s.getSiteName());
        vo.setSiteNotice(s.getSiteNotice());
        vo.setAboutContent(s.getAboutContent());
        vo.setLinksJson(s.getLinksJson());
        vo.setSeoTitle(s.getSeoTitle());
        vo.setSeoKeywords(s.getSeoKeywords());
        vo.setSeoDescription(s.getSeoDescription());
        vo.setFooterText(s.getFooterText());
        return vo;
    }
}
