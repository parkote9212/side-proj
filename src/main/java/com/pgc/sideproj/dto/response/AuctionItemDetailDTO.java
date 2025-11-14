package com.pgc.sideproj.dto.response;

import com.pgc.sideproj.dto.db.AuctionHistoryDTO;
import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import com.pgc.sideproj.dto.onbid.OnbidBasicInfoDTO;
import com.pgc.sideproj.dto.onbid.OnbidFileInfoResponseDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class AuctionItemDetailDTO {
    private AuctionMasterDTO masterInfo;
    private List<AuctionHistoryDTO> priceHistory;

    private BasicInfoResponseDTO basicInfo; // 담당자 정보
    private List<OnbidFileInfoResponseDTO.OnbidFileInfoDTO> fileList; // 첨부파일 목록
}
