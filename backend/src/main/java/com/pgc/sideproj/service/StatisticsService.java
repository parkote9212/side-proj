package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.stats.CategoryStatsDTO;
import com.pgc.sideproj.dto.stats.DashboardStatsDTO;
import com.pgc.sideproj.dto.stats.RegionStatsDTO;
import com.pgc.sideproj.mapper.StatisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsMapper statisticsMapper;

    /**
     * 대시보드에 필요한 모든 통계 요약 데이터를 조회합니다.
     * @return DashboardStatsDTO (지역별 평균 가격, 카테고리별 개수 포함)
     */
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardSummary() {

        // 지역별 평균 가격 조회
        List<RegionStatsDTO> regionAvgPrices = statisticsMapper.getRegionAveragePrice();

        // 카테고리별 물건 개수 조회
        List<CategoryStatsDTO> categoryCounts = statisticsMapper.getCategoryCounts();

        // 두 결과를 하나의 DTO로 묶어 반환
        return DashboardStatsDTO.builder()
                .regionAvgPrices(regionAvgPrices)
                .categoryCounts(categoryCounts)
                .build();
    }
}