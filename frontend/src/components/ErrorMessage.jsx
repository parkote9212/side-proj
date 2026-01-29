import PropTypes from "prop-types";
import React from "react";

/**
 * 에러 메시지 컴포넌트
 * 
 * 화면 상단에 에러 메시지를 표시합니다.
 * 
 * @component
 * @param {Object} props - 컴포넌트 props
 * @param {string} props.message - 에러 메시지
 * @param {Function} props.onClose - 닫기 핸들러
 * @returns {JSX.Element|null} 에러 메시지 컴포넌트 또는 null
 */
function ErrorMessage({ message, onClose }) {
  if (!message) return null;

  return (
    <div
      className="fixed top-4 right-4 bg-red-50 border border-red-200 rounded-lg p-4 shadow-lg z-50 max-w-md"
      role="alert"
      aria-live="assertive"
    >
      <div className="flex items-start">
        <div className="flex-shrink-0">
          <svg
            className="h-5 w-5 text-red-400"
            viewBox="0 0 20 20"
            fill="currentColor"
            aria-hidden="true"
          >
            <path
              fillRule="evenodd"
              d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.28 7.22a.75.75 0 00-1.06 1.06L8.94 10l-1.72 1.72a.75.75 0 101.06 1.06L10 11.06l1.72 1.72a.75.75 0 101.06-1.06L11.06 10l1.72-1.72a.75.75 0 00-1.06-1.06L10 8.94 8.28 7.22z"
              clipRule="evenodd"
            />
          </svg>
        </div>
        <div className="ml-3 flex-1">
          <p className="text-sm font-medium text-red-800">{message}</p>
        </div>
        {onClose && (
          <div className="ml-4 flex-shrink-0">
            <button
              type="button"
              className="inline-flex rounded-md bg-red-50 text-red-500 hover:bg-red-100 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
              onClick={onClose}
              aria-label="에러 메시지 닫기"
            >
              <span className="sr-only">닫기</span>
              <svg
                className="h-5 w-5"
                viewBox="0 0 20 20"
                fill="currentColor"
                aria-hidden="true"
              >
                <path d="M6.28 5.22a.75.75 0 00-1.06 1.06L8.94 10l-3.72 3.72a.75.75 0 101.06 1.06L10 11.06l3.72 3.72a.75.75 0 101.06-1.06L11.06 10l3.72-3.72a.75.75 0 00-1.06-1.06L10 8.94 6.28 5.22z" />
              </svg>
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

ErrorMessage.propTypes = {
  message: PropTypes.string,
  onClose: PropTypes.func,
};

export default ErrorMessage;
