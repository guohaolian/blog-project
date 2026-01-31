package com.example.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class UploadsWebConfig implements WebMvcConfigurer {

    private final String uploadDir;

    public UploadsWebConfig(@Value("${app.upload.dir:./uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /uploads/** -> local disk directory (app.upload.dir)
        //
        // - dev (Windows): ./uploads (relative to working dir)
        // - prod (Linux): /opt/blog/uploads
        //
        // Spring expects a "file:" URI.
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        String location = dir.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                // keep it simple; browser will revalidate as needed
                .setCachePeriod(3600)
                .resourceChain(false);
    }
}
