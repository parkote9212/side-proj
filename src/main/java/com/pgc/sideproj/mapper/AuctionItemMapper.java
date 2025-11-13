package com.pgc.sideproj.mapper;

import com.pgc.sideproj.dto.db.AuctionHistoryDTO;
import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import com.pgc.sideproj.dto.response.AuctionItemSummaryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AuctionItemMapper {

    void upsertMaster(AuctionMasterDTO master);
    void upsertHistory(AuctionHistoryDTO history);

    List<AuctionItemSummaryDTO> findItems(
            @Param("keyword") String keyword,
            @Param("region") String region,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    Optional<AuctionMasterDTO> findMasterByCltrNo(String cltrNo);
    List<AuctionHistoryDTO> findHistoryByCltrNo(String cltrNo);

    // --- (2) Phase 2-3 추가: 검색 결과 총 개수 ---
    int countItems(
            @Param("keyword") String keyword,
            @Param("region") String region
    );
}
