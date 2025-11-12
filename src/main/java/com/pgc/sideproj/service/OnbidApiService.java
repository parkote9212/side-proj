package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.onbid.OnbidApiResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnbidApiService {

    private final WebClient onbidWebClient;
    private final String serviceKey;

    public OnbidApiService(@Qualifier("onbidWebClient") WebClient onbidWebClient,
                          @Value("${onbid.api.serviceKey}") String serviceKey) {
        this.onbidWebClient = onbidWebClient;
        this.serviceKey = serviceKey;
        log.info("OnbidApiService initialized with serviceKey: {}", serviceKey != null ? "[PRESENT]" : "[NULL]");
    }


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


        try {
            OnbidApiResponseDTO responseDTO = onbidWebClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/getKamcoPbctCltrList")
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("pageNo", pageNo)
                            .queryParam("numOfRows", numOfRows)
                            .queryParam("DPSL_MTD_CD", "0001")
                            .build())
                    .retrieve()
                    .bodyToMono(OnbidApiResponseDTO.class)
                    .block();
            
            log.info("XML 자동 파싱 완료 - totalCount: {}", 
                responseDTO != null && responseDTO.getBody() != null ? responseDTO.getBody().getTotalCount() : "null");
            
            return responseDTO;
        } catch (Exception e) {
            log.error("Onbid API 호출 또는 XML 파싱 중 오류 발생", e);
            throw new RuntimeException("Onbid API 처리 중 오류 발생", e);
        }
    }
}
