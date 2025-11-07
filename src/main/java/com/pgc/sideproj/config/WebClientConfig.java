package com.pgc.sideproj.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${onbid.api.baseUrl}")
    private String onbidBaseUrl;

    @Value("${kakao.api.baseUrl}")
    private String kakaoBaseUrl;

    @Bean
    public WebClient onbidWebClient() {
        log.info("OnBid BaseURL: {}", onbidBaseUrl);
        String actualUrl = (onbidBaseUrl != null && !onbidBaseUrl.isEmpty()) ? onbidBaseUrl : "http://openapi.onbid.co.kr/openapi/services/KamcoPblsalThingInquireSvc";
        log.info("Using URL: {}", actualUrl);
        
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .responseTimeout(Duration.ofSeconds(30))
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS)));
        
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
                    configurer.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
                    configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024); // 2MB
                })
                .build();
        
        return WebClient.builder()
                .baseUrl(actualUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .defaultHeaders(httpHeaders -> {
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
