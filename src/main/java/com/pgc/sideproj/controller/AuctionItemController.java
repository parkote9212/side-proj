package com.pgc.sideproj.controller;

import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import com.pgc.sideproj.dto.response.PageResponseDTO;
import com.pgc.sideproj.service.AuctionItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class AuctionItemController {

    private final AuctionItemService auctionItemService;

    /**
     * [GET] /api/v1/items : 공매 물건 목록을 조회합니다. (FTS 검색 및 페이지네이션)
     *
     * @param keyword 검색어 (Optional)
     * @param page    페이지 번호 (Default: 1)
     * @param size    페이지 크기 (Default: 10)
     * @return PageResponseDTO<AuctionMasterDTO>
     */
    @GetMapping
    public ResponseEntity<PageResponseDTO<AuctionMasterDTO>> getAuctionItems(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        // 1. 페이지/사이즈 값 보정 (최소 1)
        page = Math.max(page, 1);
        size = Math.max(size, 1);

        // 2. 서비스 호출
        PageResponseDTO<AuctionMasterDTO> response =
                auctionItemService.getItems(keyword, page, size);

        // 3. 200 OK 응답
        return ResponseEntity.ok(response);
    }
}
