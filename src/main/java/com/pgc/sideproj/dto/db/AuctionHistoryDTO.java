package com.pgc.sideproj.dto.db;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionHistoryDTO {

    // 물건이력번호 (VARCHAR(50), PK)
    private String cltrHstrNo;

    // 물건번호 (VARCHAR(50), FK)
    private String cltrNo;

    // 최저입찰가 (BIGINT)
    // DB의 BIGINT는 Java의 Long 타입과 매핑됩니다.
    private Long minBidPrc;

    // 감정가 (BIGINT)
    private Long apslAsesAvgAmt;

    // 입찰시작일시 (DATETIME)
    // DB의 DATETIME 타입은 Java 8의 LocalDateTime과 가장 잘 호환됩니다.
    private LocalDateTime pbctBegnDtm;

    // 입찰마감일시 (DATETIME)
    private LocalDateTime pbctClsDtm;

    // 물건상태 (VARCHAR(100))
    private String pbctCltrStatNm;
}
