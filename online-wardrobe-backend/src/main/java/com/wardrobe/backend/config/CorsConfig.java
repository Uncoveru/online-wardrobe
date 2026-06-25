package com.wardrobe.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 跨域 + 静态资源映射 + SPA 前端回退
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // 上传目录路径（application.yml: upload.path，默认 upload/images）
    @Value("${upload.path:upload/images}")
    private String uploadPath;

    // CORS 配置：允许 localhost:7070/7071 跨域访问 /api/** 和 /images/**
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:7070", "http://localhost:7071")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
        registry.addMapping("/images/**")
                .allowedOrigins("http://localhost:7070", "http://localhost:7071")
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*");
    }

    // 静态资源映射：/images/** → 本地上传目录，其他 → 前端 dist 目录
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath + "/");
        registry.addResourceHandler("/**")
                .addResourceLocations("file:../online-wardrobe-user/dist/");
    }

    // SPA 回退：非文件路径全部转发到 index.html
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/{spring:[^.]*}")
                .setViewName("forward:/index.html");
    }
}
