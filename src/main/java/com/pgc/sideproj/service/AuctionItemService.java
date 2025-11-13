// AuctionItemService.java
package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.db.AuctionHistoryDTO;
import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import com.pgc.sideproj.dto.response.AuctionItemDetailDTO;
import com.pgc.sideproj.dto.response.AuctionItemSummaryDTO;
import com.pgc.sideproj.dto.response.PageResponseDTO;
import com.pgc.sideproj.mapper.AuctionItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // C(UD)가 없으므로 readOnly = true
public class AuctionItemService {

    private final AuctionItemMapper auctionItemMapper;

    /**
     * FTS 검색 및 페이지네이션을 적용하여 물건 목록을 조회합니다.
     *
     * @param keyword 검색어 (FTS 대상)
     * @param page    현재 페이지 (1부터 시작)
     * @param size    페이지 크기
     * @return 페이지네이션 결과 DTO (PageResponseDTO)
     */
    public PageResponseDTO<AuctionItemSummaryDTO> getItems(String keyword, int page, int size) {

        // 1. offset 계산
        int offset = (page - 1) * size;

        // 2. DB에서 총 개수 조회 (FTS 검색어 포함)
        int totalCount = auctionItemMapper.countItems(keyword);

        // 3. DB에서 데이터 목록 조회 (FTS 검색어, 페이지네이션 포함)
        List<AuctionItemSummaryDTO> items = auctionItemMapper.findItems(keyword, offset, size);

        // 4. PageResponseDTO로 래핑하여 반환
        return new PageResponseDTO<>(items, page, size, totalCount);
    }

    public AuctionItemDetailDTO getItemDetail(String cltrNo) {
        AuctionMasterDTO master = auctionItemMapper.findMasterByCltrNo(cltrNo)
                .orElseThrow(() -> new RuntimeException("물건 정보를 찾을 수 없습니다."));

        List<AuctionHistoryDTO> history = auctionItemMapper.findHistoryByCltrNo(cltrNo);

        return AuctionItemDetailDTO.builder()
                .masterInfo(master)
                .priceHistory(history)
                .build();

    }
}