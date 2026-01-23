import axios from "axios";

// 백엔드 서버의 기본 URL로 Axios 인스턴스를 생성합니다.
// 개발 환경에서는 CORS 문제가 발생할 수 있으므로, Vite 프록시 설정을 사용하는 것이 권장됩니다.
// (Vite 프록시를 사용한다면 baseURL을 '/api'와 같이 상대 경로로 설정할 수도 있습니다.)

const instance = axios.create({
    // 환경 변수에서 API Base URL을 읽어오고, 없으면 기본값 사용
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',

    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

/**
 * 요청 인터셉터 설정 (JWT 연동의 핵심)
 * 모든 HTTP 요청이 서버로 전송되기 전에 실행됩니다.
 */

instance.interceptors.request.use(
  (config) => {
    // Local Storage에서 JWT 토큰을 읽어옵니다.
    const token = localStorage.getItem("accessToken");

    if (token) {
      // Spring Security와 JwtAuthenticationFilter가 기대하는 "Bearer <토큰>" 포맷
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    // 요청 오류가 발생하면 (예: 네트워크 오류 등)
    return Promise.reject(error);
  }
);

/**
 * 응답 인터셉터 설정
 * 서버로부터 받은 응답을 처리하기 전에 실행됩니다.
 */
instance.interceptors.response.use(
  (response) => {
    // 정상 응답은 그대로 반환
    return response;
  },
  (error) => {
    // 401 Unauthorized: 토큰 만료 또는 인증 실패
    if (error.response?.status === 401) {
      // 토큰 제거
      localStorage.removeItem("accessToken");

      // Zustand 스토어에서도 토큰 제거 (순환 참조 방지를 위해 직접 처리)
      // 로그인 페이지로 리다이렉트
      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }

    // 403 Forbidden: 권한 없음
    if (error.response?.status === 403) {
      // 필요시 권한 오류 처리
      // logger는 순환 참조 방지를 위해 여기서는 사용하지 않음
      if (import.meta.env.DEV) {
        console.error("접근 권한이 없습니다.");
      }
    }

    return Promise.reject(error);
  }
);

export default instance;
