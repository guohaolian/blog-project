package com.example.blog.config;

import com.example.blog.security.JwtAuthFilter;
import com.example.blog.security.RestAccessDeniedHandler;
import com.example.blog.security.RestAuthEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Value("${app.jwt.secret}") String jwtSecret,
            RestAuthEntryPoint restAuthEntryPoint,
            RestAccessDeniedHandler restAccessDeniedHandler
    ) throws Exception {
        http
                .csrf().disable()
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(restAuthEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .authorizeRequests(auth -> auth
                        .antMatchers("/api/health").permitAll()
                        .antMatchers("/api/admin/auth/login").permitAll()
                        .antMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthFilter(jwtSecret), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
