package com.example.blog.service;

import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCodes;
import com.example.blog.vo.UploadResultVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class UploadService {

    private static final long MAX_SIZE_BYTES = 5L * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyyMM");

    private final String uploadDir;

    public UploadService(@Value("${app.upload.dir:./uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public UploadResultVO uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException(ErrorCodes.BAD_REQUEST, "file is empty");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new BizException(ErrorCodes.BAD_REQUEST, "file too large (max 5MB)");
        }

        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BizException(ErrorCodes.BAD_REQUEST, "unsupported content-type");
        }

        String ext = guessExt(file.getOriginalFilename(), contentType);
        String ym = LocalDateTime.now().format(YM);
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;

        Path dir = Paths.get(uploadDir).resolve(ym);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new BizException(ErrorCodes.SYSTEM_ERROR, "failed to create upload dir");
        }

        Path path = dir.resolve(filename);
        try {
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new BizException(ErrorCodes.SYSTEM_ERROR, "failed to save file: " + e.getMessage());
        }

        UploadResultVO vo = new UploadResultVO();
        vo.setUrl("/uploads/" + ym + "/" + filename);
        vo.setOriginalName(file.getOriginalFilename());
        vo.setSize(file.getSize());
        vo.setContentType(contentType);
        return vo;
    }

    private String guessExt(String originalName, String contentType) {
        String ext = null;
        if (StringUtils.hasText(originalName) && originalName.contains(".")) {
            ext = StringUtils.getFilenameExtension(originalName);
        }
        if (ext == null) {
            if ("image/png".equals(contentType)) {
                ext = "png";
            } else if ("image/gif".equals(contentType)) {
                ext = "gif";
            } else if ("image/webp".equals(contentType)) {
                ext = "webp";
            } else {
                ext = "jpg";
            }
        }

        ext = ext.toLowerCase(Locale.ROOT);
        // normalize jpeg
        if (ext.equals("jpeg")) ext = "jpg";
        // only allow a small set
        if (!Set.of("jpg", "png", "gif", "webp").contains(ext)) {
            ext = "jpg";
        }
        return ext;
    }
}
