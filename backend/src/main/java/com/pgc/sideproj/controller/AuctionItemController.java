package com.pgc.sideproj.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pgc.sideproj.dto.request.SearchItemRequest;
import com.pgc.sideproj.dto.response.AuctionItemDetailDTO;
import com.pgc.sideproj.dto.response.AuctionItemSummaryDTO;
import com.pgc.sideproj.dto.response.PageResponseDTO;
import com.pgc.sideproj.service.AuctionItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class AuctionItemController {

    private final AuctionItemService auctionItemService;

    /**
     * [GET] /api/v1/items : 공매 물건 목록을 조회합니다. (FTS 검색 및 페이지네이션)
     *
     * @param request 검색 요청 DTO (keyword, region, page, size)
     * @return PageResponseDTO<AuctionItemSummaryDTO>
     */
    @GetMapping
    public ResponseEntity<PageResponseDTO<AuctionItemSummaryDTO>> getAuctionItems(
            @Valid @ModelAttribute SearchItemRequest request) {
        // 기본값 설정
        String keyword = request.getKeyword() != null ? request.getKeyword() : "";
        String region = request.getRegion() != null ? request.getRegion() : "";
        int page = request.getPage() != null ? request.getPage() : 1;
        int size = request.getSize() != null ? request.getSize() : 10;

        // 페이지/사이즈 값 보정 (최소 1)
        page = Math.max(page, 1);
        size = Math.max(size, 1);

        // 서비스 호출
        PageResponseDTO<AuctionItemSummaryDTO> response = auctionItemService.getItems(keyword, region, page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cltr_no}")
    public ResponseEntity<AuctionItemDetailDTO> getItemDetail(
            @PathVariable("cltr_no") String cltrNo) {
        AuctionItemDetailDTO detail = auctionItemService.getItemDetail(cltrNo);
        return ResponseEntity.ok(detail);
    }
}
