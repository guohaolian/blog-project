package com.example.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCodes;
import com.example.blog.common.TimeUtil;
import com.example.blog.dto.admin.AdminUserCreateRequest;
import com.example.blog.dto.admin.AdminUserResetPasswordRequest;
import com.example.blog.dto.admin.AdminUserStatusRequest;
import com.example.blog.entity.AdminUser;
import com.example.blog.mapper.AdminUserMapper;
import com.example.blog.vo.admin.AdminUserVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final AdminUserMapper adminUserMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminUserService(AdminUserMapper adminUserMapper) {
        this.adminUserMapper = adminUserMapper;
    }

    public List<AdminUserVO> list() {
        List<AdminUser> list = adminUserMapper.selectList(
                new LambdaQueryWrapper<AdminUser>().orderByDesc(AdminUser::getId)
        );
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    public Map<String, Object> create(AdminUserCreateRequest req) {
        AdminUser exists = adminUserMapper.selectOne(
                new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getUsername, req.getUsername()).last("LIMIT 1")
        );
        if (exists != null) {
            throw new BizException(ErrorCodes.BAD_REQUEST, "username already exists");
        }

        LocalDateTime now = LocalDateTime.now();
        AdminUser u = new AdminUser();
        u.setUsername(req.getUsername());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setDisplayName(req.getDisplayName());
        u.setStatus(1);
        u.setCreatedAt(now);
        u.setUpdatedAt(now);

        adminUserMapper.insert(u);
        return Map.of("id", u.getId());
    }

    public Map<String, Object> resetPassword(Long id, AdminUserResetPasswordRequest req) {
        AdminUser u = adminUserMapper.selectById(id);
        if (u == null) {
            throw new BizException(ErrorCodes.NOT_FOUND, "admin not found");
        }
        u.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        u.setUpdatedAt(LocalDateTime.now());
        adminUserMapper.updateById(u);
        return Map.of("reset", true);
    }

    public Map<String, Object> updateStatus(Long id, AdminUserStatusRequest req) {
        if (req.getStatus() == null || (req.getStatus() != 0 && req.getStatus() != 1)) {
            throw new BizException(ErrorCodes.BAD_REQUEST, "invalid status");
        }
        AdminUser u = adminUserMapper.selectById(id);
        if (u == null) {
            throw new BizException(ErrorCodes.NOT_FOUND, "admin not found");
        }
        u.setStatus(req.getStatus());
        u.setUpdatedAt(LocalDateTime.now());
        adminUserMapper.updateById(u);
        return Map.of("updated", true);
    }

    private AdminUserVO toVO(AdminUser u) {
        AdminUserVO vo = new AdminUserVO();
        vo.setId(u.getId());
        vo.setUsername(u.getUsername());
        vo.setDisplayName(u.getDisplayName());
        vo.setStatus(u.getStatus());
        vo.setCreatedAt(u.getCreatedAt() == null ? null : TimeUtil.format(u.getCreatedAt()));
        vo.setUpdatedAt(u.getUpdatedAt() == null ? null : TimeUtil.format(u.getUpdatedAt()));
        return vo;
    }
}
