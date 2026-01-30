package com.example.blog.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

public class JwtTokenService {

    private final SecretKey key;
    private final long expireSeconds;

    public JwtTokenService(String secret, long expireSeconds) {
        // jjwt requires >= 256-bit key for HS256
        // If secret is shorter, we'll pad via bytes; in prod use a long random secret.
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(normalizeTo32Bytes(bytes));
        this.expireSeconds = expireSeconds;
    }

    public String createAdminToken(Long adminId, String username) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expireSeconds);

        return Jwts.builder()
                .setSubject(String.valueOf(adminId))
                .claim("username", username)
                .claim("roles", "ADMIN")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private static byte[] normalizeTo32Bytes(byte[] src) {
        byte[] out = new byte[32];
        for (int i = 0; i < out.length; i++) {
            out[i] = src[i % src.length];
        }
        return out;
    }
}
