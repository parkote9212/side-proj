package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.onbid.OnbidApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class OnbidApiService {

    public static final Logger log = LoggerFactory.getLogger(OnbidApiService.class);

    private final WebClient onbidWebClient;

    @Value("${onbid.api.service-key}")
    private String serviceKey;


    /**
     * Onbid API (캠코 공매물건 목록 조회)를 호출합니다.
     *
     * @param pageNo    페이지 번호
     * @param numOfRows 한 페이지당 행 수
     * @return OnbidApiResponseDTO API 응답을 파싱한 DTO 객체
     */
    public OnbidApiResponseDTO fetchOnbidData(int pageNo, int numOfRows){
        log.info("Onbid API 호출 시작 - pageNo: {}, numOfRows: {}", pageNo, numOfRows);

        // 1. UriComponentsBuilder를 사용하여 동적 URI 생성
        // (서비스 키에 특수문자가 포함되어 인코딩 문제가 발생할 경우,
        //  .queryParam("serviceKey", serviceKey) 대신
        //  .queryParam("serviceKey", URLDecoder.decode(serviceKey, StandardCharsets.UTF_8))
        //  등의 처리가 필요할 수 있습니다.)
        final URI uri = UriComponentsBuilder
                .fromPath("/getKamcoPbctCltrList") // 1. API 요청 경로
                .queryParam("serviceKey", serviceKey)     // 2. 주입받은 서비스 키
                .queryParam("pageNo", pageNo)         // 3. 파라미터로 받은 페이지 번호
                .queryParam("numOfRows", numOfRows)   // 4. 파라미터로 받은 행 수
                .queryParam("DPSL_MTD_CD", "00")   // 5. 고정 파라미터 예시 (매각/임대)
                // .queryParam("CLTR_MNMT_NO", "2024000001") // (필요시 다른 고정/동적 파라미터 추가)
                .build(true) // 쿼리 파라미터를 UTF-8로 인코딩합니다.
                .toUri();

        log.debug("요청 URI: {}", uri);

        try {
            // WebClient를 사용하여 API 호출
            return onbidWebClient
                    .get() // GET 요청
                    .uri(uri) // 생성된 URI 사용
                    .accept(MediaType.APPLICATION_XML) // 3. XML 응답 수신 설정
                    .retrieve() // 4. 요청 실행 및 응답 수신
                    .onStatus( // 5. 4xx (클라이언트) 또는 5xx (서버) 에러 처리
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Onbid API 에러 발생. Status: {}, Body: {}", clientResponse.statusCode(), errorBody);
                                        // 비즈니스 로직에 맞는 커스텀 예외를 던지는 것이 좋습니다.
                                        return Mono.error(new RuntimeException("Onbid API 호출 실패: " + clientResponse.statusCode()));
                                    })
                    )
                    .bodyToMono(OnbidApiResponseDTO.class) // 6. 응답 본문을 DTO로 변환
                    .block();
        } catch (Exception e) {
            log.error("Onbid API 호출 중 예기치 않은 오류 발생", e);
            // 실제 애플리케이션에서는 예외를 좀 더 구체적으로 처리해야 합니다.
            throw new RuntimeException("Onbid API 처리 중 오류 발생", e);

        }
    }
}
