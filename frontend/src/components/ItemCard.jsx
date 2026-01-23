import PropTypes from "prop-types";
import React from "react";

function ItemCard({ item, isSaved, onSelect, onSaveToggle, token }) {
  const formatDate = (dateString) => {
    if (!dateString) return "";
    return new Date(dateString).toISOString().split("T")[0];
  };

  return (
    <li className="p-3 border border-gray-200 rounded hover:bg-blue-50 transition">
      <div className="cursor-pointer" onClick={() => onSelect(item.cltrNo)}>
        {/* 1. 물건명 */}
        <p className="text-lg font-semibold text-blue-700">{item.cltrNm}</p>

        {/* 2. 카테고리 */}
        <p className="text-sm text-gray-500 mb-2">{item.ctgrFullNm}</p>

        {/* 3. 최신 최저/최고가 */}
        <div className="text-sm">
          <span className="text-gray-600">최신 입찰가: </span>
          <span className="font-bold text-red-600">
            {item.minBidPrc
              ? item.minBidPrc.toLocaleString()
              : "정보없음"}
            원
          </span>
          <span className="text-gray-500"> ~ </span>
          <span className="font-bold text-gray-700">
            {item.apslAsesAvgAmt
              ? item.apslAsesAvgAmt.toLocaleString()
              : "정보없음"}
            원
          </span>
        </div>

        {/* 4. 최신 입찰일자 */}
        <div className="text-sm text-gray-600 mt-1">
          <span>입찰 기간: </span>
          <span>{formatDate(item.pbctBegnDtm)}</span>
          <span> ~ </span>
          <span>{formatDate(item.pbctClsDtm)}</span>
        </div>
      </div>

      {/* 찜하기 버튼 - 로그인 상태일 때만 표시 */}
      {token && (
        <button
          onClick={(e) => onSaveToggle(e, item.cltrNo)}
          className={`w-full mt-2 py-1 rounded text-sm font-medium transition
          ${isSaved
              ? "bg-red-100 text-red-600 hover:bg-red-200"
              : "bg-green-100 text-green-600 hover:bg-green-200"
            }`}
          aria-label={isSaved ? "찜 취소" : "찜하기"}
        >
          {isSaved ? "찜 취소" : "찜하기"}
        </button>
      )}
    </li>
  );
}

ItemCard.propTypes = {
  item: PropTypes.shape({
    cltrNo: PropTypes.string.isRequired,
    cltrNm: PropTypes.string.isRequired,
    ctgrFullNm: PropTypes.string,
    minBidPrc: PropTypes.number,
    apslAsesAvgAmt: PropTypes.number,
    pbctBegnDtm: PropTypes.string,
    pbctClsDtm: PropTypes.string,
  }).isRequired,
  isSaved: PropTypes.bool.isRequired,
  onSelect: PropTypes.func.isRequired,
  onSaveToggle: PropTypes.func.isRequired,
  token: PropTypes.string,
};

export default React.memo(ItemCard);
