import React from "react";
import PropTypes from "prop-types";

/**
 * 지역 목록 상수
 * @constant {Array<{name: string, value: string}>}
 */
const REGIONS = [
  { name: "전체", value: "" },
  { name: "서울특별시", value: "서울특별시" },
  { name: "경기도", value: "경기도" },
  { name: "인천광역시", value: "인천광역시" },
  { name: "강원특별자치도", value: "강원특별자치도" },
  { name: "충청남도", value: "충청남도" },
  { name: "충청북도", value: "충청북도" },
  { name: "대전광역시", value: "대전광역시" },
  { name: "세종특별자치시", value: "세종특별자치시" },
  { name: "전북특별자치도", value: "전북특별자치도" },
  { name: "전라남도", value: "전라남도" },
  { name: "광주광역시", value: "광주광역시" },
  { name: "경상북도", value: "경상북도" },
  { name: "경상남도", value: "경상남도" },
  { name: "대구광역시", value: "대구광역시" },
  { name: "울산광역시", value: "울산광역시" },
  { name: "부산광역시", value: "부산광역시" },
  { name: "제주특별자치도", value: "제주특별자치도" },
];

/**
 * 검색 필터 컴포넌트
 * 
 * 지역 선택 및 키워드 검색 기능을 제공합니다.
 * 
 * @component
 * @param {Object} props - 컴포넌트 props
 * @param {string} props.keyword - 검색 키워드
 * @param {Function} props.setKeyword - 키워드 설정 함수
 * @param {string} props.region - 선택된 지역
 * @param {Function} props.setRegion - 지역 설정 함수
 * @param {Function} props.onSearch - 검색 실행 함수
 * @returns {JSX.Element} 검색 필터 컴포넌트
 */
function SearchFilter({ keyword, setKeyword, region, setRegion, onSearch }) {
  return (
    <div className="mb-4 space-y-2">
      <select
        value={region}
        onChange={(e) => setRegion(e.target.value)}
        className="w-full p-2 border rounded-md"
        aria-label="지역 선택"
      >
        {REGIONS.map((r) => (
          <option key={r.name} value={r.value}>
            {r.name}
          </option>
        ))}
      </select>

      <div className="flex gap-2">
        <input
          type="text"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && onSearch()}
          placeholder="물건명, 주소 등 키워드 검색"
          className="flex-grow p-2 border rounded-md"
          aria-label="물건 검색어 입력"
          aria-describedby="search-help"
        />
        <span id="search-help" className="sr-only">
          주소, 물건명 등으로 검색할 수 있습니다
        </span>
        <button
          onClick={onSearch}
          className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
          aria-label="물건 검색"
          title="검색어를 입력하고 엔터를 누르거나 검색 버튼을 클릭하세요"
        >
          검색
        </button>
      </div>
    </div>
  );
}

SearchFilter.propTypes = {
  keyword: PropTypes.string.isRequired,
  setKeyword: PropTypes.func.isRequired,
  region: PropTypes.string.isRequired,
  setRegion: PropTypes.func.isRequired,
  onSearch: PropTypes.func.isRequired,
};

export default React.memo(SearchFilter);
