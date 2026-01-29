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

/**
 * 공매 물건 조회 API 컨트롤러입니다.
 * 
 * <p>공매 물건 목록 조회 및 상세 정보 조회 기능을 제공합니다.
 * 목록 조회는 Full-Text Search(FTS)를 지원하며 페이지네이션이 적용됩니다.
 * 
 * @author sideproj
 * @since 1.0
 */
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

    /**
     * 공매 물건의 상세 정보를 조회합니다.
     * 
     * <p>물건의 기본 정보, 가격 이력, 담당자 정보, 첨부 파일 목록 등을 포함합니다.
     * 온비드 API를 호출하여 추가 정보를 가져옵니다.
     * 
     * @param cltrNo 공매 물건 번호 (cltr_no)
     * @return 공매 물건 상세 정보 DTO
     * @throws com.pgc.sideproj.exception.custom.ResourceNotFoundException 해당 물건을 찾을 수 없는 경우
     */
    @GetMapping("/{cltr_no}")
    public ResponseEntity<AuctionItemDetailDTO> getItemDetail(
            @PathVariable("cltr_no") String cltrNo) {
        AuctionItemDetailDTO detail = auctionItemService.getItemDetail(cltrNo);
        return ResponseEntity.ok(detail);
    }
}
