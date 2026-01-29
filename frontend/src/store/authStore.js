import { create } from 'zustand';

/**
 * Local Storage에서 초기 토큰 값을 읽어오는 함수
 * @returns {string|null} 저장된 토큰 또는 null
 */
const getInitialToken = () => {
    return localStorage.getItem('accessToken') || null;
};

/**
 * 인증 상태 관리 스토어
 * 
 * JWT 토큰을 관리하고 Local Storage와 동기화합니다.
 * 
 * @type {Object}
 */
const useAuthStore = create((set) => ({
    token: getInitialToken(),
    
    /**
     * 토큰 설정
     * 
     * 로그인 시 호출하여 토큰을 스토어와 Local Storage에 저장합니다.
     * 
     * @param {string} newToken - 서버에서 발급받은 JWT Access Token
     */
    setToken: (newToken) => {
        set({ token: newToken }); 
        localStorage.setItem('accessToken', newToken);
    },
    
    /**
     * 토큰 삭제
     * 
     * 로그아웃 시 호출하여 토큰을 스토어와 Local Storage에서 삭제합니다.
     */
    clearToken: () => {
        set({ token: null });
        localStorage.removeItem('accessToken');
    }
}));

export default useAuthStore;