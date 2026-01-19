package com.pgc.sideproj.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BasicInfoResponseDTO {
    // Java camelCase 필드명
    private String plnmNm;
    private String rsbyDept;
    private String pscgNm;
    private String pscgTpno;
    private String pscgEmalAdrs;
}