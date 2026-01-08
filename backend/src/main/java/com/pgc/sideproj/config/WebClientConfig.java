package com.pgc.sideproj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${onbid.api.base-url}")
    private String onbidBaseUrl;

    @Value("${kakao.api.base-url}")
    private String kakaoBaseUrl;

    @Bean
    public WebClient onbidWebClient() {
        return WebClient.builder()
                .baseUrl(onbidBaseUrl)
                .defaultHeaders(httpHeaders -> {
                    // Onbid API는 XML 응답을 기대합니다.
                    httpHeaders.setContentType(MediaType.APPLICATION_XML);
                    httpHeaders.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_XML));
                })
                .build();
    }

    /**
     * Kakao API 호출용 WebClient 빈을 생성합니다.
     * 빈 이름: kakaoWebClient
     */
    @Bean(name = "kakaoWebClient")
    public WebClient kakaoWebClient() {
        return WebClient.builder()
                .baseUrl(kakaoBaseUrl)
                .defaultHeaders(httpHeaders -> {
                    // Kakao API는 JSON 응답을 기대합니다.
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
                })
                .build();
    }
}
