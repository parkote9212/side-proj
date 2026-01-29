package com.pgc.sideproj.controller;

import com.pgc.sideproj.dto.stats.DashboardStatsDTO;
import com.pgc.sideproj.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 통계 데이터를 제공하는 컨트롤러입니다.
 * 
 * <p>대시보드에 필요한 통계 정보를 조회할 수 있습니다.
 * 
 * @author sideproj
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 대시보드에 필요한 통계 요약 데이터를 조회합니다.
     * 
     * <p>지역별 평균 가격 및 카테고리별 물건 개수 등의 통계 정보를 반환합니다.
     * 
     * @return 대시보드 통계 요약 데이터 (DashboardStatsDTO)
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardStatsDTO> getDashboardSummary() {
        DashboardStatsDTO summary = statisticsService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}