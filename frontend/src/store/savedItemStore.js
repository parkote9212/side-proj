import { create } from 'zustand';
import { fetchMySavedItems, addSavedItem, deleteSavedItem } from '../api/savedItemApi';
import { logger } from '../utils/logger';

/**
 * 찜하기 상태 관리 스토어
 * 
 * 찜한 물건의 ID 목록을 관리하고 API와 동기화합니다.
 * 
 * @type {Object}
 */
const useSavedItemStore = create((set) => ({
  savedItemIds: [],

  /**
   * 찜 목록 조회
   * 
   * 서버에서 찜 목록을 가져와 상태를 업데이트합니다.
   */
  fetchSaved: async () => {
    try {
      const savedItems = await fetchMySavedItems();
      const idArray = savedItems.map(item => item.cltrNo);
      set({ savedItemIds: idArray });
    } catch (error) {
      logger.error("찜 목록 로딩 실패:", error);
      set({ savedItemIds: [] });
    }
  },

  /**
   * 찜하기
   * 
   * 서버에 찜하기 요청을 보내고 성공 시 상태를 업데이트합니다.
   * 
   * @param {string} cltrNo - 물건 번호
   */
  addSaved: async (cltrNo) => {
    try {
      await addSavedItem(cltrNo);
      set((state) => ({
        savedItemIds: [...state.savedItemIds, cltrNo]
      }));
    } catch (error) {
      logger.error("찜하기 실패:", error);
    }
  },

  /**
   * 찜 취소
   * 
   * 서버에 찜 취소 요청을 보내고 성공 시 상태를 업데이트합니다.
   * 
   * @param {string} cltrNo - 물건 번호
   */
  removeSaved: async (cltrNo) => {
    try {
      await deleteSavedItem(cltrNo);
      set((state) => ({
        savedItemIds: state.savedItemIds.filter(id => id !== cltrNo)
      }));
    } catch (error) {
      logger.error("찜 취소 실패:", error);
    }
  }
}));

export default useSavedItemStore;
