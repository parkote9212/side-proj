package com.pgc.sideproj.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 카카오 주소 검색 API의 최상위 응답 DTO입니다.
 */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAddressResponseDTO {
    private List<DocumentDTO> documents;

    /**
     * 검색된 주소 문서 상세 DTO (좌표 포함)
     */
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DocumentDTO {

        // y: 위도 (latitude)
        @JsonProperty("y")
        private BigDecimal latitude;

        // x: 경도 (longitude)
        @JsonProperty("x")
        private BigDecimal longitude;

        // 주소 정보 (디버깅 또는 상세 정보 필요 시 사용 가능)
        @JsonProperty("address_name")
        private String addressName;
    }
}