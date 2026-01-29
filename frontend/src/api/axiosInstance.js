import axios from "axios";

/**
 * Axios 인스턴스 생성
 * 
 * 백엔드 API 호출을 위한 기본 설정이 포함된 Axios 인스턴스입니다.
 * JWT 토큰을 자동으로 요청 헤더에 추가합니다.
 * 
 * @constant {AxiosInstance}
 */
const instance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

/**
 * 요청 인터셉터
 * 
 * 모든 HTTP 요청 전에 JWT 토큰을 Authorization 헤더에 추가합니다.
 */
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * 응답 인터셉터
 * 
 * 서버 응답을 처리하고, 401/403 오류 시 자동으로 로그인 페이지로 리다이렉트합니다.
 */
instance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("accessToken");

      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }

    if (error.response?.status === 403) {
      if (import.meta.env.DEV) {
        console.error("접근 권한이 없습니다.");
      }
    }

    return Promise.reject(error);
  }
);

export default instance;
