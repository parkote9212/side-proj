package com.pgc.sideproj.mapper;

import com.pgc.sideproj.dto.stats.CategoryStatsDTO;
import com.pgc.sideproj.dto.stats.RegionStatsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatisticsMapper {
    /**
     * 지역별(시/도) 물건의 평균 최저 입찰가를 조회합니다.
     */
    List<RegionStatsDTO> getRegionAveragePrice();

    /**
     * 카테고리별 물건 개수를 조회합니다.
     */
    List<CategoryStatsDTO> getCategoryCounts();
}
