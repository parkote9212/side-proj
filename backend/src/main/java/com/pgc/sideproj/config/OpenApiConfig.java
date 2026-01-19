package com.pgc.sideproj.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "JWT Bearer Token";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 1. API 정보 설정
                .info(new Info()
                        .title("Side Project REST API")
                        .version("v1.0")
                        .description("공매 물건 조회 및 찜하기 서비스 백엔드 API 명세서"))

                // 2. Security Scheme (인증 방식) 정의
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP) // 인증 타입: HTTP
                                .scheme("bearer") // 인증 스키마: Bearer
                                .bearerFormat("JWT") // Bearer 포맷: JWT 지정
                                .description("JWT 토큰을 입력해 주세요. (예: Bearer abc.xyz.123)")
                        )
                )

                // 3. Global Security Requirement (보안 요구사항) 적용
                // 모든 엔드포인트에 이 보안 스키마를 기본으로 적용
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}