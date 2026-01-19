package com.pgc.sideproj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**") // /api/v1 경로로 들어오는 모든 요청에 대해
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173") // 프론트엔드 주소 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // 인증 정보(쿠키, Authorization 헤더) 허용
    }
}