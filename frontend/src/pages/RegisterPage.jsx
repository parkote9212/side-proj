import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { registerUser } from '../api/authApi';
import { logger } from '../utils/logger';

/**
 * 회원가입 페이지 컴포넌트
 * 
 * 새로운 사용자 등록 기능을 제공합니다.
 * 
 * @component
 * @returns {JSX.Element} 회원가입 페이지
 */
const RegisterPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [nickname, setNickname] = useState('');
    const [regError, setRegError] = useState(null);
    const navigate = useNavigate();

    /**
     * 회원가입 폼 제출 핸들러
     * @param {Event} e - 이벤트 객체
     */
    const handleSubmit = async (e) => {
        e.preventDefault();
        setRegError(null);

        try {
            await registerUser({ email, password, nickname });

            alert("회원가입이 완료되었습니다! 로그인 페이지로 이동합니다.");
            navigate("/login");

        } catch (error) {
            logger.error("회원가입 실패:", error.response?.data || error);
            setRegError(error.response?.data?.message || "회원가입에 실패했습니다. 입력 정보를 확인해주세요.");
        }
    };

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-100">
            <div className="p-8 bg-white shadow-xl rounded-lg w-full max-w-md">
                {/* 페이지 제목 영역 */}
                <h1 className="text-3xl font-bold mb-6 text-center text-gray-800">회원가입</h1>

                {/* 에러 메시지 영역 */}
                {regError && (
                    <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
                        {regError}
                    </div>
                )}

                {/* 회원가입 폼 영역 */}
                <form onSubmit={handleSubmit} className="space-y-4">
                    {/* 이메일 입력 영역 */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">이메일 (ID)</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            className="w-full p-2 border border-gray-300 rounded-md"
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
                            className="w-full p-2 border border-gray-300 rounded-md"
                        />
                    </div>
                    {/* 닉네임 입력 영역 */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">닉네임</label>
                        <input
                            type="text"
                            value={nickname}
                            onChange={(e) => setNickname(e.target.value)}
                            required
                            className="w-full p-2 border border-gray-300 rounded-md"
                        />
                    </div>
                    {/* 가입하기 버튼 영역 */}
                    <button
                        type="submit"
                        className="w-full bg-green-600 text-white p-2 rounded-md hover:bg-green-700 transition duration-150"
                    >
                        가입하기
                    </button>
                </form>
            </div>
        </div>
    );
};

export default RegisterPage;
