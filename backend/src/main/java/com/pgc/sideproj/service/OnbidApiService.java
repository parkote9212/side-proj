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

/**
 * 온비드(Onbid) 공매 API를 호출하는 서비스입니다.
 * 
 * <p>캠코 공매물건 목록 조회, 기본정보 상세조회, 첨부파일 조회 등의 API를 제공합니다.
 * XML 응답을 파싱하여 DTO로 변환합니다.
 * 
 * @author sideproj
 * @since 1.0
 */
@Service
public class OnbidApiService {

    private static final Logger log = LoggerFactory.getLogger(OnbidApiService.class);

    private final WebClient onbidWebClient;
    private final String serviceKey;
    private final XmlMapper xmlMapper;


    /**
     * 생성자 주입을 통해 온비드 WebClient와 서비스 키를 주입받습니다.
     * 
     * @param onbidWebClient WebClientConfig에서 정의한 onbidWebClient 빈
     * @param serviceKey application.yml에 정의된 온비드 API 서비스 키
     */
    public OnbidApiService(@Qualifier("onbidWebClient") WebClient onbidWebClient,
                          @Value("${onbid.api.serviceKey}") String serviceKey) {
        this.onbidWebClient = onbidWebClient;
        this.serviceKey = serviceKey;
        this.xmlMapper = new XmlMapper();
        log.info("OnbidApiService 초기화 완료 - 서비스 키: {}", serviceKey != null ? "[존재함]" : "[없음]");
    }



    /**
     * 온비드 API를 호출하여 공매물건 목록을 조회합니다.
     * 
     * <p>캠코 공매물건 목록 조회 API를 호출하여 XML 응답을 받아 DTO로 변환합니다.
     * 페이지네이션을 지원하며, 한 번의 호출로 최대 numOfRows개의 항목을 조회할 수 있습니다.
     *
     * @param pageNo    페이지 번호 (1부터 시작)
     * @param numOfRows 한 페이지당 행 수
     * @return 온비드 API 응답을 파싱한 DTO 객체
     * @throws RuntimeException API 호출 또는 XML 파싱 중 오류가 발생한 경우
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
            
            log.info("온비드 API XML 응답 수신 완료");
            
            // 2. Jackson XmlMapper로 수동 파싱
            OnbidApiResponseDTO responseDTO = xmlMapper.readValue(xmlResponse, OnbidApiResponseDTO.class);
            
            log.info("Jackson XML 파싱 완료 - 총 개수: {}", 
                responseDTO != null && responseDTO.getBody() != null ? responseDTO.getBody().getTotalCount() : "null");
            
            return responseDTO;
        } catch (Exception e) {
            log.error("Onbid API 호출 또는 XML 파싱 중 오류 발생", e);
            throw new RuntimeException("Onbid API 처리 중 오류 발생", e);
        }
    }

    /**
     * 캠코 공매공고 기본정보 상세조회 API를 호출합니다.
     * 
     * <p>공매물건의 담당자 정보, 연락처 등의 기본 정보를 조회합니다.
     * 
     * @param plnmNo 공고번호
     * @param pbctNo 공매번호
     * @return 기본정보 상세조회 응답 DTO (실패 시 null)
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
     * 캠코 공매공고 첨부파일 상세조회 API를 호출합니다.
     * 
     * <p>공매물건의 첨부파일 목록을 조회합니다.
     * 
     * @param plnmNo 공고번호
     * @param pbctNo 공매번호
     * @return 첨부파일 정보 응답 DTO (실패 시 null)
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
