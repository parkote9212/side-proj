package com.pgc.sideproj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${onbid.api.base-url}")
    private String baseUrl;

    @Bean
    public WebClient onbidWebClient(){
        return WebClient.builder()
                .baseUrl(baseUrl) // 주입받은 baseUrl을 기본 URL로 설정합니다.
                .build(); //
    }

}
