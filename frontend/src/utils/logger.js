/**
 * 환경별 로깅 유틸리티
 * 개발 환경에서만 콘솔에 로그를 출력하고, 프로덕션에서는 에러 리포팅 서비스로 전송
 */

const isDev = import.meta.env.DEV;

export const logger = {
  error: (...args) => {
    if (isDev) {
      console.error(...args);
    } else {
      // 프로덕션에서는 에러 리포팅 서비스로 전송
      // 예: Sentry, LogRocket 등
      // TODO: 에러 리포팅 서비스 연동
    }
  },

  warn: (...args) => {
    if (isDev) {
      console.warn(...args);
    }
  },

  info: (...args) => {
    if (isDev) {
      console.info(...args);
    }
  },

  log: (...args) => {
    if (isDev) {
      console.log(...args);
    }
  },

  debug: (...args) => {
    if (isDev) {
      console.debug(...args);
    }
  }
};
