package com.example.workrecode.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置类
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 配置跨域请求
     * @param registry 跨域注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 设置允许跨域的路径
                .allowedOriginPatterns("*") // 设置允许跨域请求的域名
                .allowCredentials(true) // 设置允许携带凭证
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 设置允许的请求方法
                .allowedHeaders("*") // 设置允许的请求头
                .maxAge(3600); // 设置预检请求的缓存时间
    }
}
