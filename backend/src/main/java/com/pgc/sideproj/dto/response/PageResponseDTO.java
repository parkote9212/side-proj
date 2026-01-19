// PageResponseDTO.java
package com.pgc.sideproj.dto.response;

import lombok.Getter;
import java.util.List;

@Getter
public class PageResponseDTO<T> {

    // 1. 현재 페이지의 데이터 목록 (e.g., List<AuctionMasterDTO>)
    private final List<T> data;

    // 2. 페이징 정보
    private final PageInfo pageInfo;

    // 생성자
    public PageResponseDTO(List<T> data, int currentPage, int size, long totalCount) {
        this.data = data;
        this.pageInfo = new PageInfo(currentPage, size, totalCount);
    }

    // (내부 클래스) 페이징 정보 DTO
    @Getter
    private static class PageInfo {
        private final int currentPage;   // 현재 페이지
        private final int size;          // 페이지 크기
        private final long totalCount;   // 전체 아이템 개수
        private final int totalPage;     // 전체 페이지 수

        public PageInfo(int currentPage, int size, long totalCount) {
            this.currentPage = currentPage;
            this.size = size;
            this.totalCount = totalCount;
            // 전체 페이지 수 계산
            this.totalPage = (int) Math.ceil((double) totalCount / size);
        }
    }
}