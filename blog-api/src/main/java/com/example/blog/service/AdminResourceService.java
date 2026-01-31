package com.example.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCodes;
import com.example.blog.common.PageResult;
import com.example.blog.common.TimeUtil;
import com.example.blog.entity.FileResource;
import com.example.blog.mapper.FileResourceMapper;
import com.example.blog.vo.UploadResultVO;
import com.example.blog.vo.admin.FileResourceVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class AdminResourceService {

    private final FileResourceMapper fileResourceMapper;
    private final String uploadDir;

    public AdminResourceService(FileResourceMapper fileResourceMapper,
                               @Value("${app.upload.dir:./uploads}") String uploadDir) {
        this.fileResourceMapper = fileResourceMapper;
        this.uploadDir = uploadDir;
    }

    public void recordUpload(UploadResultVO upload) {
        if (upload == null || upload.getUrl() == null || upload.getUrl().trim().isEmpty()) return;

        FileResource fr = new FileResource();
        fr.setUrl(upload.getUrl());
        fr.setOriginalName(upload.getOriginalName());
        fr.setSize(upload.getSize() == null ? 0L : upload.getSize());
        fr.setContentType(upload.getContentType());
        fr.setCreatedAt(LocalDateTime.now());

        try {
            fileResourceMapper.insert(fr);
        } catch (Exception ignored) {
            // ignore duplicate url insert
        }
    }

    public PageResult<FileResourceVO> page(long pageNum, long pageSize, String keyword, String contentTypePrefix) {
        LambdaQueryWrapper<FileResource> qw = new LambdaQueryWrapper<FileResource>().orderByDesc(FileResource::getId);

        if (StringUtils.hasText(keyword)) {
            String k = keyword.trim();
            qw.and(w -> w.like(FileResource::getUrl, k).or().like(FileResource::getOriginalName, k));
        }

        if (StringUtils.hasText(contentTypePrefix)) {
            qw.likeRight(FileResource::getContentType, contentTypePrefix.trim());
        }

        Page<FileResource> page = fileResourceMapper.selectPage(new Page<>(pageNum, pageSize), qw);

        return new PageResult<>(
                page.getRecords().stream().map(this::toVO).collect(Collectors.toList()),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
    }

    public void delete(Long id) {
        FileResource fr = fileResourceMapper.selectById(id);
        if (fr == null) {
            throw new BizException(ErrorCodes.NOT_FOUND, "resource not found");
        }

        // delete db record first
        fileResourceMapper.deleteById(id);

        // best-effort delete disk file
        try {
            String url = fr.getUrl();
            if (url != null && url.startsWith("/uploads/")) {
                String rel = url.substring("/uploads/".length());
                Path p = Paths.get(uploadDir).resolve(rel);
                Files.deleteIfExists(p);
            }
        } catch (Exception ignored) {
        }
    }

    private FileResourceVO toVO(FileResource fr) {
        FileResourceVO vo = new FileResourceVO();
        vo.setId(fr.getId());
        vo.setUrl(fr.getUrl());
        vo.setOriginalName(fr.getOriginalName());
        vo.setSize(fr.getSize());
        vo.setContentType(fr.getContentType());
        vo.setCreatedAt(fr.getCreatedAt() == null ? null : TimeUtil.format(fr.getCreatedAt()));
        return vo;
    }
}
