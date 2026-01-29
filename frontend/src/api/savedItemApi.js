import api from './axiosInstance';

/**
 * 내 찜 목록 조회 API
 * 
 * GET /api/v1/saved-items
 * 
 * @returns {Promise<Array>} AuctionMasterDTO 배열
 */
export const fetchMySavedItems = async () => {
  const response = await api.get('/saved-items');
  return response.data;
};

/**
 * 찜하기 API
 * 
 * POST /api/v1/saved-items/{item_id}
 * 
 * @param {string} cltrNo - 물건 번호
 * @returns {Promise<Object>} 응답 데이터
 */
export const addSavedItem = async (cltrNo) => {
  const response = await api.post(`/saved-items/${cltrNo}`);
  return response.data;
};

/**
 * 찜 취소 API
 * 
 * DELETE /api/v1/saved-items/{item_id}
 * 
 * @param {string} cltrNo - 물건 번호
 * @returns {Promise<Object>} 응답 데이터
 */
export const deleteSavedItem = async (cltrNo) => {
  const response = await api.delete(`/saved-items/${cltrNo}`);
  return response.data;
};