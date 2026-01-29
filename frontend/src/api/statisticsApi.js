import api from './axiosInstance';
import { logger } from '../utils/logger';

/**
 * 통계 요약 정보 조회 API
 * 
 * GET /api/v1/statistics/summary
 * 
 * @returns {Promise<Object>} DashboardStatsDTO { regionAvgPrices: [], categoryCounts: [] }
 * @throws {Error} API 호출 실패 시 에러
 */
export async function fetchStatistics() {
    try {
        const response = await api.get("/statistics/summary");

        return response.data;

    } catch (error) {
        logger.error("통계 데이터 조회 실패:", error);
        throw error;
    }
}
