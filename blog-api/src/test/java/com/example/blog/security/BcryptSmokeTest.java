package com.example.blog.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BcryptSmokeTest {

    @Test
    void seededPasswordHashShouldMatchAdmin123() {
        // hash from deploy/sql/init.sql
        String hash = "$2a$10$sjGLhXaPfA4Ykj7B7b/Jb.6LDks4bWeTFDjbN3nKsg.e4FWCSCn06";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("admin123", hash));
    }
}
