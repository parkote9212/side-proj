package com.pgc.sideproj.config;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${onbid.api.baseUrl}")
    private String onbidBaseUrl;

    @Value("${kakao.api.baseUrl}")
    private String kakaoBaseUrl;


    @Bean
    public WebClient onbidWebClient() {
        log.info("온비드 기본 URL: {}", onbidBaseUrl);
        String actualUrl = (onbidBaseUrl != null && !onbidBaseUrl.isEmpty()) ? onbidBaseUrl : "http://openapi.onbid.co.kr/openapi/services/KamcoPblsalThingInquireSvc";
        log.info("사용할 URL: {}", actualUrl);
        
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                .responseTimeout(Duration.ofSeconds(60))
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(60, TimeUnit.SECONDS)));
        
        XmlMapper xmlMapper = new XmlMapper();
        
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(xmlMapper, MediaType.APPLICATION_XML, MediaType.TEXT_XML));
                    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(xmlMapper, MediaType.APPLICATION_XML, MediaType.TEXT_XML));
                    configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024); // 2MB
                })
                .build();
        
        return WebClient.builder()
                .baseUrl(actualUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setAccept(java.util.Arrays.asList(MediaType.APPLICATION_XML, MediaType.TEXT_XML));
                })
                .build();
    }


    /**
     * Kakao API 호출용 WebClient 빈을 생성합니다.
     * 빈 이름: kakaoWebClient
     */
    @Bean(name = "kakaoWebClient")
    public WebClient kakaoWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));
        
        return WebClient.builder()
                .baseUrl(kakaoBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
                })
                .build();
    }
}
