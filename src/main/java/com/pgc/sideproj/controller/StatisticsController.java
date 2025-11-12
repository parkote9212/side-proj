package com.pgc.sideproj.controller;

import com.pgc.sideproj.dto.stats.DashboardStatsDTO;
import com.pgc.sideproj.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * [GET] /api/v1/statistics/summary : 대시보드 통계 요약 데이터를 반환합니다.
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardStatsDTO> getDashboardSummary() {
        DashboardStatsDTO summary = statisticsService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}