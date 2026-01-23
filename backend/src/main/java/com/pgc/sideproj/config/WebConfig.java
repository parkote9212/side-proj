package com.pgc.sideproj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = allowedOrigins.split(",");

        registry.addMapping("/api/v1/**") // /api/v1 경로로 들어오는 모든 요청에 대해
                .allowedOrigins(origins) // 환경 변수로 설정 가능한 프론트엔드 주소 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Authorization") // 클라이언트에서 접근 가능한 헤더
                .allowCredentials(true) // 인증 정보(쿠키, Authorization 헤더) 허용
                .maxAge(3600); // preflight 요청 캐시 시간 (1시간)
    }
}
