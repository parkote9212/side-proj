package com.pgc.sideproj.service;

// API DTO와 DB DTO의 패키지 경로를 확인하세요.

import com.pgc.sideproj.dto.onbid.OnbidItemDTO;
import com.pgc.sideproj.dto.db.AuctionHistoryDTO;
import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Onbid API 응답(OnbidItemDTO)을 DB 저장용 DTO(AuctionMasterDTO, AuctionHistoryDTO)로
 * 변환하고 데이터를 정제하는 서비스입니다.
 */
@Slf4j
@Service
public class DataCleansingService {

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
                .plnmNo(item.getPlnmNo())
                .pbctNo(item.getPbctNo())
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
     * Geocoding에 방해가 될 수 있는 괄호, 쉼표(,), 외 필지 정보, 아파트 동/호수 등
     * 불필요한 상세 정보를 제거하고 핵심 지번까지만 남깁니다.
     *
     * @param rawAddress API에서 받은 원본 주소 문자열
     * @return 정제된 주소 문자열
     */
    private String cleanseAddress(String rawAddress) {
        if (rawAddress == null || rawAddress.isBlank()) {
            return "";
        }

        String cleaned = rawAddress.trim();

        // 1. 괄호와 그 안의 모든 내용 제거 (토지/건물 명시, 좌권 매수 정보 등)
        cleaned = cleaned.replaceAll("\\([^\\)]*\\)", "").trim(); // (토지), (건물) 등 제거
        cleaned = cleaned.replaceAll("\\[[^\\]]*\\]", "").trim(); // [일좌권1매...] 등 대괄호 내용 제거

        // 2. '외', '제', '총', '내', '보관', '출자증권' 등의 키워드와 그 이후의 상세 정보 제거
        // 복잡한 보관 물품/출자증권 정보는 대부분 "보관", "출자증권", "내" 이후에 나옴.
        cleaned = cleaned.replaceAll(" 제\\d+층.*| 제\\d+동.*| 외\\s*\\d*필지.*| 총\\s*\\d*좌.*", "").trim();

        // 🚨 키워드 뒤의 정보 강력 제거: "금천세무서 보관중인 전문건설공제조합..." -> "금천세무서"만 남김
        cleaned = cleaned.replaceAll("\\s보관중인.*| 출자증권.*| 내\\s*보관.*", "").trim();


        // 3. 쉼표(,) 뒤의 다중 지번 또는 상세 정보 제거 (가장 강력한 정제)
        // 핵심 주소는 첫 번째 쉼표 앞에 있을 확률이 높으므로, 첫 번째 쉼표 이후를 모두 제거합니다.
        // 예: 589-1 , 589-2, 589-3 => 589-1만 남김
        int commaIndex = cleaned.indexOf(',');
        if (commaIndex != -1) {
            // 첫 번째 쉼표가 나타나면, 쉼표 이후는 모두 버립니다.
            cleaned = cleaned.substring(0, commaIndex).trim();
        }

        // 4. 최종적으로 남은 문자열의 양쪽 공백 제거
        return cleaned.trim();
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
