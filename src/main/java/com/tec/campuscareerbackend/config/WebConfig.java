package com.tec.campuscareerbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 匹配所有 URL 路径，包括图片资源
                .allowedOrigins("*") // 允许的前端地址，设置为 "*" 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 确保 OPTIONS 方法支持预检请求
                .allowedHeaders("*") // 允许的请求头，支持所有头部
                .exposedHeaders("Content-Disposition") // 暴露的响应头，便于文件下载
                .allowCredentials(false) // 关闭携带 Cookie（对静态资源跨域加载无必要）
                .maxAge(3600); // 设置预检请求的缓存时间（单位：秒）
    }
}
