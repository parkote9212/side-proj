import React from 'react';
import useAuthStore from '../store/authStore';
import { Navigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import { logger } from '../utils/logger';

/**
 * 관리자 라우트 보호 컴포넌트
 * 
 * ADMIN 권한이 있는 사용자만 접근할 수 있도록 보호합니다.
 * 
 * @component
 * @param {Object} props - 컴포넌트 props
 * @param {React.ReactNode} props.children - 보호할 컴포넌트
 * @returns {JSX.Element} 관리자 페이지 또는 리다이렉트
 */
const AdminRoute = ({ children }) => {
  const { token } = useAuthStore((state) => state);

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  try {
    const decoded = jwtDecode(token);
    const role = decoded.role;

    if (role !== 'ADMIN') {
      return <Navigate to="/" replace />;
    }

    return children;

  } catch (e) {
    logger.error("JWT Decode Error", e);
    return <Navigate to="/login" replace />;
  }
};

export default AdminRoute;
