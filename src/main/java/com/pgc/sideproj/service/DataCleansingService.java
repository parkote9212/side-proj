package com.pgc.sideproj.service;

// API DTO와 DB DTO의 패키지 경로를 확인하세요.

import com.pgc.sideproj.dto.onbid.OnbidItemDTO;
import com.pgc.sideproj.dto.db.AuctionHistoryDTO;
import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Onbid API 응답(OnbidItemDTO)을 DB 저장용 DTO(AuctionMasterDTO, AuctionHistoryDTO)로
 * 변환하고 데이터를 정제하는 서비스입니다.
 */
@Service
public class DataCleansingService {

    private static final Logger log = LoggerFactory.getLogger(DataCleansingService.class);

    // Onbid API의 날짜/시간 형식 ("YYYYMMDDHHMMSS")
    private static final DateTimeFormatter ONBID_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    // Onbid 상세 페이지 URL 접두사
    private static final String ONBID_DETAIL_URL_PREFIX =
            "https://www.onbid.co.kr/op/cta/cltr/cltrView.do?cltrCltrNo=";

    /**
     * OnbidItemDTO에서 불변 데이터(마스터)를 추출하여 AuctionMasterDTO를 생성합니다.
     * 주소 정제, URL 생성 로직이 포함됩니다.
     *
     * @param item API에서 응답받은 원본 아이템
     * @return DB 저장을 위한 AuctionMasterDTO
     */
    public AuctionMasterDTO createMasterFrom(OnbidItemDTO item) {
        if (item == null) {
            return null;
        }

        // 3. 상세 URL 생성
        String detailUrl = ONBID_DETAIL_URL_PREFIX + item.getCltrNo();

        return AuctionMasterDTO.builder()
                // 4. 필드 매핑
                .cltrNo(item.getCltrNo())
                .cltrNm(item.getCltrNm())
                .ctgrFullNm(item.getCtgrFullNm()) // 카테고리
                .ldnmAdrs(item.getLdnmAdrs()) // 원본 지번주소
                .nmrdAdrs(item.getNmrdAdrs()) // 원본 도로명주소

                // 1. 주소 정제 로직 적용
                .clnLdnmAdrs(cleanseAddress(item.getLdnmAdrs()))
                .clnNmrdAdrs(cleanseAddress(item.getNmrdAdrs()))

                .onbidDetailUrl(detailUrl)

                // 위도, 경도는 이 단계에서는 null이며, 이후 Geocoding 단계에서 채워집니다.
                .latitude(null)
                .longitude(null)
                .build();
    }

    /**
     * OnbidItemDTO에서 가변 데이터(이력)를 추출하여 AuctionHistoryDTO를 생성합니다.
     * 날짜/시간 변환, 금액 변환 로직이 포함됩니다.
     *
     * @param item API에서 응답받은 원본 아이템
     * @return DB 저장을 위한 AuctionHistoryDTO
     */
    public AuctionHistoryDTO createHistoryFrom(OnbidItemDTO item) {
        if (item == null) {
            return null;
        }

        return AuctionHistoryDTO.builder()
                // 4. 필드 매핑
                .cltrHstrNo(item.getCltrHstrNo()) // 물건이력번호 (PK)
                .cltrNo(item.getCltrNo())         // 물건번호 (FK)
                .pbctCltrStatNm(item.getPbctCltrStatNm()) // 물건상태

                // 2. 날짜/시간 변환 로직
                .pbctBegnDtm(parseOnbidDateTime(item.getPbctBegnDtm()))
                .pbctClsDtm(parseOnbidDateTime(item.getPbctClsDtm()))

                // (추가) 금액(String)을 Long으로 변환
                .minBidPrc(item.getMinBidPrc())
                .apslAsesAvgAmt(item.getApslAsesAvgAmt())

                .build();
    }


    // --- Helper Methods ---

    /**
     * 1. 주소 정제 로직
     * Geocoding에 방해가 될 수 있는 괄호와 내용, 앞뒤 공백을 제거합니다.
     *
     * @param rawAddress API에서 받은 원본 주소 문자열
     * @return 정제된 주소 문자열
     */
    private String cleanseAddress(String rawAddress) {
        if (rawAddress == null || rawAddress.isBlank()) {
            return ""; // null 대신 빈 문자열 반환
        }

        // 1. 양쪽 공백 제거
        // 2. 정규식을 사용하여 괄호와 그 안의 모든 내용 (예: (상세주소), (아파트)) 제거
        // 3. 괄호 제거 후 남을 수 있는 앞뒤 공백 재제거
        return rawAddress.trim()
                .replaceAll("\\([^\\)]*\\)", "")
                .trim();
    }

    /**
     * 2. 날짜/시간 변환 로직
     * Onbid API의 "yyyyMMddHHmmss" 형식 문자열을 LocalDateTime 객체로 변환합니다.
     *
     * @param dateTimeString API에서 받은 날짜/시간 문자열
     * @return LocalDateTime 객체 (변환 실패 시 null)
     */
    private LocalDateTime parseOnbidDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateTimeString, ONBID_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            log.warn("Onbid API 날짜 형식 파싱 실패: '{}'. 원인: {}", dateTimeString, e.getMessage());
            return null; // 파싱 실패 시 DB에 null로 저장
        }
    }
}
