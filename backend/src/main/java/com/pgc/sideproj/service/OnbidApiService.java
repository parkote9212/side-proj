
package com.pgc.sideproj.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.pgc.sideproj.dto.onbid.OnbidApiResponseDTO;
import com.pgc.sideproj.dto.onbid.OnbidBasicInfoDTO;
import com.pgc.sideproj.dto.onbid.OnbidBasicInfoResponseDTO;
import com.pgc.sideproj.dto.onbid.OnbidFileInfoResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OnbidApiService {

    private static final Logger log = LoggerFactory.getLogger(OnbidApiService.class);

    private final WebClient onbidWebClient;
    private final String serviceKey;
    private final XmlMapper xmlMapper;


public OnbidApiService(@Qualifier("onbidWebClient") WebClient onbidWebClient,
                          @Value("${onbid.api.serviceKey}") String serviceKey) {
        this.onbidWebClient = onbidWebClient;
        this.serviceKey = serviceKey;
        this.xmlMapper = new XmlMapper();
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
            // 1. WebClient로 XML 문자열 받기
            String xmlResponse = onbidWebClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/getKamcoPbctCltrList")
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("pageNo", pageNo)
                            .queryParam("numOfRows", numOfRows)
                            .queryParam("DPSL_MTD_CD", "0001")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            log.info("OnBid API XML 응답 수신 완료");
            
            // 2. Jackson XmlMapper로 수동 파싱
            OnbidApiResponseDTO responseDTO = xmlMapper.readValue(xmlResponse, OnbidApiResponseDTO.class);
            
            log.info("Jackson XML 파싱 완료 - totalCount: {}", 
                responseDTO != null && responseDTO.getBody() != null ? responseDTO.getBody().getTotalCount() : "null");
            
            return responseDTO;
        } catch (Exception e) {
            log.error("Onbid API 호출 또는 XML 파싱 중 오류 발생", e);
            throw new RuntimeException("Onbid API 처리 중 오류 발생", e);
        }
    }

    /**
     * [추가] 캠코공매공고 기본정보 상세조회 API 호출
     */

    public OnbidBasicInfoResponseDTO fetchBasicInfoDetail(String plnmNo, String pbctNo){
        log.info("Onbid 상세정보 API 호출 - PLNM_NO: {}, PBCT_NO: {}", plnmNo, pbctNo);
        try {
            return onbidWebClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/getKamcoPlnmPbctBasicInfoDetail")
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("PLNM_NO", plnmNo)
                            .queryParam("PBCT_NO", pbctNo)
                            .build())
                    .retrieve()
                    .bodyToMono(OnbidBasicInfoResponseDTO.class)
                    .block();
        } catch (Exception e){
            log.error("Onbid 상세정보 API 호출 실패", e);
            return null;
        }
    }

/**
     * [추가] 캠코공매공고 첨부파일 상세조회 API 호출
     */

    public OnbidFileInfoResponseDTO fetchFileInfoDetail(String plnmNo, String pbctNo) {
        log.info("Onbid 첨부파일 API 호출 - PLNM_NO: {}, PBCT_NO: {}", plnmNo, pbctNo);
        try {
            return onbidWebClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/getKamcoPlnmPbctFileInfoDetail") // [cite: 68]
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("PLNM_NO", plnmNo) // [cite: 70]
                            .queryParam("PBCT_NO", pbctNo) // [cite: 70]
                            .queryParam("numOfRows", 10) // [cite: 70]
                            .queryParam("pageNo", 1) // [cite: 70]
                            .build())
                    .retrieve()
                    .bodyToMono(OnbidFileInfoResponseDTO.class) // 이 DTO는 <response> 구조임 [cite: 76]
                    .block();
        } catch (Exception e) {
            log.error("Onbid 첨부파일 API 호출 실패", e);
            return null;
        }
    }


}
