package com.example.blog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_BEARER_JWT = "bearer-jwt";

    @Bean
    public OpenAPI blogOpenAPI() {
        SecurityScheme bearerJwt = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // 默认把 Bearer JWT 挂到文档里；公开接口仍可不带 token，管理端接口需要带 token。
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(SECURITY_SCHEME_BEARER_JWT);

        return new OpenAPI()
                .info(new Info()
                        .title("Blog API")
                        .version("v1")
                        .description("Auto-generated OpenAPI spec via springdoc-openapi.\n\n" +
                                "- Web APIs: /api\n" +
                                "- Admin APIs: /api/admin\n\n" +
                                "Admin APIs require: Authorization: Bearer <token>"))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080").description("Local dev")
                ))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_BEARER_JWT, bearerJwt))
                .addSecurityItem(securityRequirement);
    }
}
