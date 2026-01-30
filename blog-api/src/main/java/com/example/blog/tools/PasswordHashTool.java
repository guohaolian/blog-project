package com.example.blog.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Tiny helper you can run to generate BCrypt hashes for seed data.
 *
 * Usage (example):
 *   mvn -q -DskipTests package
 *   java -cp target/classes;target/dependency/* com.example.blog.tools.PasswordHashTool admin123
 */
public class PasswordHashTool {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: PasswordHashTool <plainPassword>");
            System.exit(1);
        }
        String plain = args[0];
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode(plain));
    }
}
