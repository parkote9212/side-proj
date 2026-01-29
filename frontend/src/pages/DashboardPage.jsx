import React, { useState, useEffect } from "react";
import { fetchStatistics } from "../api/statisticsApi";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
} from "recharts";
import { ClipLoader } from "react-spinners";

/**
 * 파이 차트에 사용할 색상 배열
 * @constant {string[]}
 */
const PIE_CHART_COLORS = [
  "#0088FE",
  "#00C49F",
  "#FFBB28",
  "#FF8042",
  "#8884d8",
  "#82ca9d",
];

/**
 * 통계 대시보드 페이지 컴포넌트
 * 
 * 지역별 평균 가격 막대 차트와 카테고리별 물건 개수 파이 차트를 표시합니다.
 * 
 * @component
 * @returns {JSX.Element} 대시보드 페이지
 */
const DashboardPage = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  /**
   * 통계 데이터를 로드하는 useEffect
   */
  useEffect(() => {
    const loadStats = async () => {
      try {
        const response = await fetchStatistics();
        setStats(response);
      } catch (err) {
        setError(err.message || "통계 데이터를 불러오는데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };

    loadStats();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <ClipLoader color="#36d7b7" size={50} />
        <p className="ml-4">통계 데이터 로드 중...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-screen">
        <h1 className="text-red-600 text-2xl">오류: {error}</h1>
      </div>
    );
  }

  if (
    !stats ||
    (!stats.regionAvgPrices?.length && !stats.categoryCounts?.length)
  ) {
    return (
      <div className="flex justify-center items-center h-screen">
        <p className="text-gray-500 text-xl">표시할 통계 데이터가 없습니다.</p>
      </div>
    );
  }

  /**
   * 카테고리별 총 물건 개수 계산
   * @type {number}
   */
  const categoryTotal = (stats?.categoryCounts?.length > 0)
  ? stats.categoryCounts.reduce((sum, entry) => sum + entry.count, 0)
  : 0;

  return (
    <div className="p-4 md:p-8 min-h-screen bg-gray-50">
      {/* 페이지 제목 영역 */}
      <h1 className="text-3xl font-bold mb-8 text-center text-gray-800">
        경매 물건 대시보드 요약
      </h1>

      {/* 차트 영역 */}
      <div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* 지역별 평균 가격 막대 차트 영역 */}
        <div className="bg-white p-6 rounded-lg shadow-xl">
          <h2 className="text-xl font-semibold mb-4 text-center">
            지역별 평균 최저 입찰가 (단위: 원)
          </h2>
          {stats.regionAvgPrices.length > 0 ? (
            <ResponsiveContainer width="100%" height={400}>
              <BarChart
                data={stats.regionAvgPrices}
                margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
              >
                <XAxis
                  dataKey="regionName"
                  angle={-45}
                  textAnchor="end"
                  height={80}
                  interval={0}
                />
                <YAxis
                  dataKey="avgPrice"
                  tickFormatter={(value) => `${(value / 1000000).toFixed(1)}M`}
                />
                <Tooltip
                  formatter={(value) => `${value.toLocaleString()}원`}
                />
                <Legend />
                <Bar dataKey="avgPrice" name="평균 최저가" fill="#4f46e5" />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <p className="text-center text-gray-500 h-96 flex items-center justify-center">
              데이터가 부족합니다.
            </p>
          )}
        </div>

        {/* 카테고리별 물건 개수 파이 차트 영역 */}
        <div className="bg-white p-6 rounded-lg shadow-xl">
          <h2 className="text-xl font-semibold mb-4 text-center">
            카테고리별 물건 개수
          </h2>
          {stats.categoryCounts.length > 0 ? (
            <ResponsiveContainer width="100%" height={400}>
              <PieChart>
                <Pie
                  data={stats.categoryCounts}
                  dataKey="count"
                  nameKey="categoryName"
                  cx="50%"
                  cy="50%"
                  outerRadius={120}
                  fill="#8884d8"
                >
                  {stats.categoryCounts.map((entry, index) => (
                    <Cell
                      key={`cell-${index}`}
                      fill={PIE_CHART_COLORS[index % PIE_CHART_COLORS.length]}
                    />
                  ))}
                </Pie>
                <Tooltip />
                <Legend
                  layout="vertical"
                  align="right"
                  verticalAlign="middle"
                  formatter={(value, entry) => {
                    const count = entry.payload.count;
                   const percent = (categoryTotal > 0)
                            ? ((count / categoryTotal) * 100).toFixed(0)
                            : 0;

                          return `${value} (${percent}%)`;
                  }}
                />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <p className="text-center text-gray-500 h-96 flex items-center justify-center">
              데이터가 부족합니다.
            </p>
          )}
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
