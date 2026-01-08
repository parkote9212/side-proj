package com.pgc.sideproj.dto.onbid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnbidItemDTO {
    // --- 물건 마스터(auction_master) 정보 ---

    @JsonProperty("CLTR_NO")
    private String cltrNo; // 물건번호 (Master PK)

    @JsonProperty("CLTR_NM")
    private String cltrNm; // 물건명

    @JsonProperty("CTGR_FULL_NM")
    private String ctgrFullNm; // 카테고리 (주소 정제 시 참고용)

    @JsonProperty("LDNM_ADRS")
    private String ldnmAdrs; // 지번주소 (원본)

    @JsonProperty("NMRD_ADRS")
    private String nmrdAdrs; // 도로명주소 (원본)


    // --- 물건 이력(auction_history) 정보 ---

    @JsonProperty("CLTR_HSTR_NO")
    private String cltrHstrNo; // 물건이력번호 (History PK)

    @JsonProperty("MIN_BID_PRC")
    private Long minBidPrc; // 최저입찰가

    @JsonProperty("APSL_ASES_AVG_AMT")
    private Long apslAsesAvgAmt; // 감정가

    @JsonProperty("FEE_RATE")
    private String feeRate; // 최저입찰가율 (e.g., "(100%)")

    @JsonProperty("PBCT_BEGN_DTM")
    private String pbctBegnDtm; // 입찰시작일시 (String으로 우선 수신)

    @JsonProperty("PBCT_CLS_DTM")
    private String pbctClsDtm; // 입찰마감일시 (String으로 우선 수신)

    @JsonProperty("PBCT_CLTR_STAT_NM")
    private String pbctCltrStatNm; // 물건상태 (e.g., "입찰준비중")

    @JsonProperty("IQRY_CNT")
    private Integer iqryCnt; // 조회수

    @JsonProperty("GOODS_NM")
    private String goodsNm; // 물건상세정보 (e.g., "건물 44.13 ㎡ ...")
}
