import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useKakaoLoader } from "react-kakao-maps-sdk";
import { BarLoader } from "react-spinners";
import { fetchItemDetail, fetchItems } from "../api/itemApi";
import ErrorMessage from "../components/ErrorMessage";
import ItemCard from "../components/ItemCard";
import ItemDetailModal from "../components/ItemDetailModal";
import ItemMap from "../components/ItemMap";
import SearchFilter from "../components/SearchFilter";
import useAuthStore from "../store/authStore";
import useSavedItemStore from "../store/savedItemStore";
import { logger } from "../utils/logger";

const KAKAO_APP_KEY = import.meta.env.VITE_KAKAO_MAP_JS_KEY;

const MainPage = () => {
  // ===== 모든 useState 선언 (최상단) =====
  const [selectedItem, setSelectedItem] = useState(null);
  const [isDetailLoading, setIsDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState(null);
  const [items, setItems] = useState([]);
  const [pageInfo, setPageInfo] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [mapCenter, setMapCenter] = useState({ lat: 37.5665, lng: 126.978 });

  // 필터용 useState
  const [inputKeyword, setInputKeyword] = useState("");
  const [activeKeyword, setActiveKeyword] = useState("");
  const [activeRegion, setActiveRegion] = useState("");

  // ===== Kakao 지도 로더 =====
  const { loading: _kakaoLoading, error: kakaoError } = useKakaoLoader({
    appkey: KAKAO_APP_KEY,
    libraries: ["services", "clusterer"],
  });

  // ===== Zustand 스토어 (개별 selector로 메모이제이션) =====
  const token = useAuthStore((state) => state.token);
  const savedItemIds = useSavedItemStore((state) => state.savedItemIds);
  const fetchSaved = useSavedItemStore((state) => state.fetchSaved);
  const addSaved = useSavedItemStore((state) => state.addSaved);
  const removeSaved = useSavedItemStore((state) => state.removeSaved);

  // ===== useRef로 초기 로드 방지 (React 18 Strict Mode 대응) =====
  const hasRunRef = useRef(false);

  // ===== 메모이제이션된 핸들러 =====
  const handleItemClick = useCallback(
    async (cltrNo) => {
      setIsDetailLoading(true);
      setSelectedItem(null);
      setDetailError(null);

      try {
        const detailData = await fetchItemDetail(cltrNo);
        setSelectedItem(detailData);
      } catch (e) {
        logger.error("상세 정보 로드 실패:", e);
        const errorMessage =
          e.response?.status === 404
            ? "물건 정보를 찾을 수 없습니다"
            : e.response?.status === 500
              ? "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요"
              : e.response?.data?.message ||
              e.message ||
              "상세 정보를 불러오는데 실패했습니다.";
        setDetailError(errorMessage);
        // 5초 후 자동으로 에러 메시지 제거
        setTimeout(() => setDetailError(null), 5000);
      } finally {
        setIsDetailLoading(false);
      }
    },
    []
  );

  const handleSearch = useCallback(() => {
    setCurrentPage(1);
    setActiveKeyword(inputKeyword);
  }, [inputKeyword]);

  const handleRegionChange = useCallback((e) => {
    setCurrentPage(1);
    setActiveRegion(e.target.value);
  }, []);

  const handleSaveToggle = useCallback(
    (e, cltrNo) => {
      e.stopPropagation();

      if (!token) {
        setError("로그인이 필요합니다.");
        setTimeout(() => setError(null), 3000);
        return;
      }

      const isSaved = savedItemIds.includes(cltrNo);

      if (isSaved) {
        removeSaved(cltrNo);
      } else {
        addSaved(cltrNo);
      }
    },
    [token, savedItemIds, removeSaved, addSaved]
  );

  // ===== useEffect: 로그인 시 찜 목록 1회 로드 =====
  useEffect(() => {
    if (token && !hasRunRef.current) {
      fetchSaved();
      hasRunRef.current = true;
    }
    if (!token) {
      hasRunRef.current = false;
    }
  }, [token, fetchSaved]);

  // ===== useEffect: 물건 목록 로드 =====
  useEffect(() => {
    const loadItems = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await fetchItems({
          page: currentPage,
          size: 10,
          keyword: activeKeyword,
          region: activeRegion,
        });

        const responseItems = response.data || [];
        setItems(responseItems);
        setPageInfo(response.pageInfo || {});

        if (responseItems.length > 0) {
          const firstItem = responseItems[0];
          setMapCenter({
            lat: firstItem.latitude,
            lng: firstItem.longitude,
          });
        }
      } catch (error) {
        const errorMessage =
          error.response?.data?.message ||
          error.message ||
          "데이터 로드 중 오류가 발생했습니다.";
        setError(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    loadItems();
  }, [currentPage, activeKeyword, activeRegion]);

  // ===== 메모이제이션된 값들 =====
  const memoizedItems = useMemo(() => items, [items]);

  // ===== 에러 처리 UI =====
  if (kakaoError) {
    return (
      <div className="flex justify-center items-center h-screen">
        <p className="text-red-600" role="alert">
          카카오맵 로드 중 오류 발생: {kakaoError}
        </p>
      </div>
    );
  }

  if (loading && items.length === 0) {
    return (
      <div className="flex justify-center items-center h-screen">
        <BarLoader color="#36d7b7" />
        <p className="ml-4 text-gray-600">데이터를 불러오는 중입니다...</p>
      </div>
    );
  }

  return (
    <div className="max-w-[1920px] mx-auto flex h-screen p-4 gap-4 bg-white">
      {/* 에러 메시지 */}
      {error && (
        <ErrorMessage
          message={error}
          onClose={() => setError(null)}
        />
      )}
      {detailError && (
        <ErrorMessage
          message={detailError}
          onClose={() => setDetailError(null)}
        />
      )}

      {/* 지도 영역 */}
      <ItemMap
        items={memoizedItems}
        mapCenter={mapCenter}
        onItemClick={handleItemClick}
      />

      {/* 목록 영역 - 1/3 너비 */}
      <div className="w-1/3 max-w-[640px] p-4 bg-white shadow rounded-lg border flex flex-col">
        <h2 className="text-2xl font-bold mb-4 text-gray-800">
          경매 물건 목록 ({pageInfo.totalCount || 0}개)
        </h2>

        {/* 필터링 및 검색 UI */}
        <SearchFilter
          keyword={inputKeyword}
          setKeyword={setInputKeyword}
          region={activeRegion}
          setRegion={setActiveRegion}
          onSearch={handleSearch}
        />

        <div className="flex-grow overflow-y-auto">
          {loading ? (
            <div className="flex justify-center items-center h-full">
              <BarLoader color="#36d7b7" />
            </div>
          ) : items.length === 0 ? (
            <p className="text-gray-500 text-center mt-10">
              조회된 물건이 없습니다.
            </p>
          ) : (
            <ul className="space-y-3">
              {items.map((item) => {
                const isSaved = savedItemIds.includes(item.cltrNo);
                return (
                  <ItemCard
                    key={item.cltrNo}
                    item={item}
                    isSaved={isSaved}
                    onSelect={handleItemClick}
                    onSaveToggle={handleSaveToggle}
                    token={token}
                  />
                );
              })}
            </ul>
          )}
        </div>

        <div className="mt-auto pt-4">
          {/* 페이지네이션 UI */}
          {pageInfo.totalPage > 1 && (
            <div className="flex justify-center items-center space-x-2">
              <button
                onClick={() => setCurrentPage(currentPage - 1)}
                disabled={currentPage === 1}
                className="px-3 py-2 rounded-md bg-white text-gray-700 hover:bg-gray-100 disabled:opacity-50"
                aria-label="이전 페이지"
              >
                이전
              </button>

              {Array.from(
                { length: Math.min(pageInfo.totalPage, 10) },
                (_, index) => {
                  const pageNum = index + 1;
                  return (
                    <button
                      key={pageNum}
                      onClick={() => setCurrentPage(pageNum)}
                      className={`px-3 py-2 rounded-md ${currentPage === pageNum
                        ? "bg-blue-600 text-white"
                        : "bg-white text-gray-700 hover:bg-gray-100"
                        }`}
                      aria-label={`${pageNum}페이지로 이동`}
                      aria-current={currentPage === pageNum ? "page" : undefined}
                    >
                      {pageNum}
                    </button>
                  );
                }
              )}

              <button
                onClick={() => setCurrentPage(currentPage + 1)}
                disabled={currentPage === pageInfo.totalPage}
                className="px-3 py-2 rounded-md bg-white text-gray-700 hover:bg-gray-100 disabled:opacity-50"
                aria-label="다음 페이지"
              >
                다음
              </button>
            </div>
          )}

          <div className="mt-2 text-center text-sm text-gray-500">
            {currentPage} / {pageInfo.totalPage || 1} 페이지 (총{" "}
            {pageInfo.totalCount || 0}개)
          </div>
        </div>
      </div>

      {/* 상세 정보 모달 */}
      <ItemDetailModal
        item={selectedItem}
        isLoading={isDetailLoading}
        onClose={() => setSelectedItem(null)}
      />
    </div>
  );
};

export default MainPage;
