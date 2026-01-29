import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { loginUser } from '../api/authApi';
import useAuthStore from '../store/authStore';
import { logger } from '../utils/logger';

/**
 * 로그인 페이지 컴포넌트
 * 
 * 사용자 로그인 기능을 제공합니다.
 * 
 * @component
 * @returns {JSX.Element} 로그인 페이지
 */
const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loginError, setLoginError] = useState(null);

    const navigate = useNavigate();
    const setToken = useAuthStore((state) => state.setToken);

    /**
     * 로그인 폼 제출 핸들러
     * @param {Event} e - 이벤트 객체
     */
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoginError(null);

        try {
            const response = await loginUser({ email, password });

            setToken(response.accessToken);
            navigate("/");

        } catch (error) {
            logger.error("로그인 실패:", error.response?.data || error);
            setLoginError(error.response?.data?.message || "로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.");
        }
    };

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-100">
            <div className="p-8 bg-white shadow-xl rounded-lg w-full max-w-md">
                {/* 페이지 제목 영역 */}
                <h1 className="text-3xl font-bold mb-6 text-center text-gray-800">로그인</h1>

                {/* 에러 메시지 영역 */}
                {loginError && (
                    <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
                        {loginError}
                    </div>
                )}

                {/* 로그인 폼 영역 */}
                <form onSubmit={handleSubmit} className="space-y-4">
                    {/* 이메일 입력 영역 */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">이메일</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            className="w-full p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>
                    {/* 비밀번호 입력 영역 */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">비밀번호</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            className="w-full p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>
                    {/* 로그인 버튼 영역 */}
                    <button
                        type="submit"
                        className="w-full bg-blue-600 text-white p-2 rounded-md hover:bg-blue-700 transition duration-150"
                    >
                        로그인
                    </button>
                    {/* 회원가입 링크 영역 */}
                    <div className="text-center text-sm mt-3">
                        <a href="/register" className="text-blue-500 hover:underline">
                            계정이 없으신가요? 회원가입
                        </a>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default LoginPage;
