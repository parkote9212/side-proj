package com.pgc.sideproj.dto.db;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionMasterDTO {

    // 물건번호 (VARCHAR(50), PK)
    private String cltrNo;

    // 물건명 (VARCHAR(1000))
    private String cltrNm;

    // 카테고리 (VARCHAR(255))
    // 스키마에는 있지만 요청 목록에서 빠져있어서 추가했습니다.
    private String ctgrFullNm;

    // 원본 지번주소 (VARCHAR(1000))
    private String ldnmAdrs;

    // 원본 도로명주소 (VARCHAR(1000))
    private String nmrdAdrs;

    // 정제된 지번주소 (VARCHAR(500))
    private String clnLdnmAdrs;

    // 정제된 도로명주소 (VARCHAR(500))
    private String clnNmrdAdrs;

    // 위도 (DECIMAL(10, 8))
    private Double latitude;

    // 경도 (DECIMAL(11, 8))
    private Double longitude;

    // 온비드 상세 URL (VARCHAR(500))
    private String onbidDetailUrl;

    private String plnmNo; // 공고번호 (PLNM_NO)

    private String pbctNo; // 공매번호 (PBCT_NO)

    // 참고: created_at, updated_at 필드는 DB에서 자동 관리되므로
    //      일반적으로 DTO에 포함시키지 않거나, 필요시 읽기 전용으로만 사용합니다.
}
