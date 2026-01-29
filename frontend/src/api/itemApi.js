import { logger } from '../utils/logger';
import api from './axiosInstance';

/**
 * 경매 물건 목록 조회 API
 * 
 * @param {Object} params - 조회 파라미터
 * @param {number} [params.page=1] - 페이지 번호
 * @param {number} [params.size=10] - 페이지 크기
 * @param {string} [params.keyword=''] - 검색 키워드
 * @param {string} [params.region=''] - 지역 필터
 * @returns {Promise<Object>} PageResponseDTO 형식의 응답
 * @throws {Error} API 호출 실패 시 에러
 */
export async function fetchItems({ page = 1, size = 10, keyword = '', region = '' }) {
    try {
        const response = await api.get("/items", {
            params: { page, size, keyword, region }
        });

        return response.data;

    } catch (error) {
        if (error.response) {
            logger.error("서버 응답 오류 (Status):", error.response.status);
            logger.error("서버 응답 본문:", error.response.data);
        }
        else if (error.request) {
            logger.error("요청은 전송되었으나 응답을 받지 못함:", error.request);
        }
        else {
            logger.error("Axios 요청 설정 오류:", error.message);
        }
        throw error;
    }

}

export const fetchItemDetail = async (cltrNo) => {
    try {
        const response = await api.get(`/items/${cltrNo}`);
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error;
        } else if (error.request) {
            throw new Error("서버에 연결할 수 없습니다");
        } else {
            throw new Error("요청 처리 중 오류가 발생했습니다");
        }
    }
};

