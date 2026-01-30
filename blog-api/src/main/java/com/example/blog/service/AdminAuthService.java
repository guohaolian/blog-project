package com.example.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCodes;
import com.example.blog.dto.admin.AdminLoginRequest;
import com.example.blog.entity.AdminUser;
import com.example.blog.mapper.AdminUserMapper;
import com.example.blog.security.JwtTokenService;
import com.example.blog.vo.admin.AdminLoginResponse;
import com.example.blog.vo.admin.AdminMeResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final JwtTokenService jwtTokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminAuthService(AdminUserMapper adminUserMapper, JwtTokenService jwtTokenService) {
        this.adminUserMapper = adminUserMapper;
        this.jwtTokenService = jwtTokenService;
    }

    public AdminLoginResponse login(AdminLoginRequest req) {
        AdminUser user = adminUserMapper.selectOne(
                new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getUsername, req.getUsername()).last("LIMIT 1")
        );
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BizException(ErrorCodes.UNAUTHORIZED, "invalid username or password");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BizException(ErrorCodes.UNAUTHORIZED, "invalid username or password");
        }

        String token = jwtTokenService.createAdminToken(user.getId(), user.getUsername());
        return new AdminLoginResponse(token);
    }

    public AdminMeResponse me(Long adminId) {
        AdminUser user = adminUserMapper.selectById(adminId);
        if (user == null) {
            throw new BizException(ErrorCodes.UNAUTHORIZED, "invalid token");
        }
        return new AdminMeResponse(user.getId(), user.getUsername(), user.getDisplayName());
    }
}
