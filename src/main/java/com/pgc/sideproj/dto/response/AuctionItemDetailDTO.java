package com.pgc.sideproj.dto.response;

import com.pgc.sideproj.dto.db.AuctionHistoryDTO;
import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AuctionItemDetailDTO {
    private AuctionMasterDTO masterInfo;
    private List<AuctionHistoryDTO> priceHistory;
}
