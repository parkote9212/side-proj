package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.kakao.KakaoAddressResponseDTO;
import com.pgc.sideproj.dto.kakao.KakaoAddressResponseDTO.DocumentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * 카카오 지도 API (Geocoding) 호출을 담당하는 서비스입니다.
 */
@Service
public class KakaoMapService {

    private static final Logger log = LoggerFactory.getLogger(KakaoMapService.class);

    private final WebClient kakaoWebClient;
    private final String kakaoApiKey;

    /**
     * 생성자 주입을 통해 kakaoWebClient 빈과 API 키를 주입받습니다.
     *
     * @param kakaoWebClient WebClientConfig에서 정의한 kakaoWebClient 빈
     * @param kakaoApiKey    application.yml에 정의된 REST API 키
     */
    public KakaoMapService(
            @Qualifier("kakaoWebClient") WebClient kakaoWebClient,
            @Value("${kakao.api.restApiKey}") String kakaoApiKey) {
        this.kakaoWebClient = kakaoWebClient;
        this.kakaoApiKey = kakaoApiKey;
    }

    /**
     * 주소를 이용하여 카카오 Geocoding API를 호출하고 좌표 정보를 가져옵니다.
     * 네트워크 오류(WebClientResponseException) 발생 시 최대 3번 재시도합니다.
     *
     * @param address 정제된 주소 문자열
     * @return 좌표 정보 (DocumentDTO) 또는 찾지 못했을 경우 null
     */
    @Retryable(
            // 재시도 대상 예외: 5xx 서버 에러 또는 연결/타임아웃 에러
            value = {WebClientResponseException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000) // 1초 간격으로 재시도
    )
    public DocumentDTO getCoordinates(String address) {
        if (address == null || address.isBlank()) {
            return null;
        }

        try {
            log.info("카카오 API Geocoding 요청 시작: {}", address);
            KakaoAddressResponseDTO response = kakaoWebClient.get()
                    // 요청 경로 및 쿼리 파라미터 설정 (query=address)
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/address.json")
                            .queryParam("query", address)
                            .build())
                    // 인증 헤더 추가 (Authorization: KakaoAK [key])
                    .header("Authorization", "KakaoAK " + kakaoApiKey)
                    // 4xx, 5xx 에러 처리 로직
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                        log.error("카카오 API 클라이언트 에러 발생: {}", clientResponse.statusCode());
                        return clientResponse.createException();
                    })
                    .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                        log.warn("카카오 API 서버 에러 발생 (재시도 가능): {}", clientResponse.statusCode());
                        return clientResponse.createException();
                    })
                    // 응답 본문을 DTO로 변환하고 블로킹
                    .bodyToMono(KakaoAddressResponseDTO.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (response == null || response.getDocuments() == null || response.getDocuments().isEmpty()) {
                log.warn("카카오 API로부터 좌표 정보를 찾을 수 없습니다: {}", address);
                return null;
            }

            // 첫 번째 검색 결과 반환
            DocumentDTO document = response.getDocuments().get(0);
            log.info("좌표 획득 성공: [{}, {}] for address: {}", document.getLatitude(), document.getLongitude(), address);
            return document;

        } catch (WebClientResponseException e) {
            log.error("WebClient 요청 중 응답 에러 발생: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            // Retryable 어노테이션이 이 예외를 잡아서 재시도를 처리합니다.
            throw e;
        } catch (Exception e) {
            log.error("카카오 API 호출 중 예상치 못한 에러 발생: {}", e.getMessage(), e);
            return null;
        }
    }
}