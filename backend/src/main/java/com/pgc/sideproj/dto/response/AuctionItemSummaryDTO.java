package com.pgc.sideproj.dto.response;

import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// AuctionMasterDTO를 상속받아 마스터 정보를 모두 포함
@Getter
@Setter
public class AuctionItemSummaryDTO extends AuctionMasterDTO {

    // --- auction_history의 "최신" 이력 정보 ---
    private Long minBidPrc;         // 최신 최저입찰가
    private Long apslAsesAvgAmt;    // 최신 감정가 (최고가 대용)
    private LocalDateTime pbctBegnDtm;   // 최신 입찰시작일
    private LocalDateTime pbctClsDtm;    // 최신 입찰마감일
    private String pbctCltrStatNm;  // 최신 물건상태
}