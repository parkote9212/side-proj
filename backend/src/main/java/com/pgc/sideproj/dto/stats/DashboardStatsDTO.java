package com.pgc.sideproj.dto.stats;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardStatsDTO {

    private List<RegionStatsDTO> regionAvgPrices;
    private List<CategoryStatsDTO> categoryCounts;

}
