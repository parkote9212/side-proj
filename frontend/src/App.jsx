import { BrowserRouter, Routes, Route } from "react-router-dom";
import Navi from "./components/Navi";
import AdminRoute from "./components/AdminRoute";
import MainPage from "./pages/MainPage";
import DashboardPage from "./pages/DashboardPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import MyPage from "./pages/MyPage";
import AdminPage from "./pages/AdminPage";

/**
 * 메인 App 컴포넌트
 * 
 * 라우팅 설정과 전체 레이아웃을 관리합니다.
 * 
 * @component
 * @returns {JSX.Element} App 컴포넌트
 */
function App() {
  return (
    <BrowserRouter>
      <div className="App h-screen flex flex-col">
        <Navi />
        <main className="flex-1 overflow-auto">
          <Routes>
            <Route path="/" element={<MainPage />} />
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/mypage" element={<MyPage />} />
            <Route
              path="*"
              element={
                <h1 className="text-4xl text-center pt-20">404 Not Found</h1>
              }
            />

            <Route
              path="/admin"
              element={
                <AdminRoute>
                  <AdminPage />
                </AdminRoute>
              }
            />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;
