package com.pgc.sideproj.dto.onbid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class OnbidBasicInfoDTO {
    @JsonProperty("PLNM_NM")
    private String plnmNm; // 공고명 [cite: 43]

    @JsonProperty("RSBY_DEPT")
    private String rsbyDept; // 담당부점 [cite: 43]

    @JsonProperty("PSCG_NM")
    private String pscgNm; // 담당자명 [cite: 43]

    @JsonProperty("PSCG_TPNO")
    private String pscgTpno; // 담당자 전화번호 [cite: 43]

    @JsonProperty("PSCG_EMAL_ADRS")
    private String pscgEmalAdrs; // 담당자 이메일 [cite: 43]

}
