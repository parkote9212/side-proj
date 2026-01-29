import React from "react";
import PropTypes from "prop-types";
import { BarLoader } from "react-spinners";

/**
 * 물건 상세 정보 모달 컴포넌트
 * 
 * 물건의 상세 정보, 담당자 정보, 첨부 파일, 가격 변동 이력을 표시합니다.
 * 
 * @component
 * @param {Object} props - 컴포넌트 props
 * @param {Object} props.item - 물건 상세 정보
 * @param {boolean} props.isLoading - 로딩 상태
 * @param {Function} props.onClose - 모달 닫기 핸들러
 * @returns {JSX.Element|null} 모달 컴포넌트 또는 null
 */
function ItemDetailModal({ item, isLoading, onClose }) {
  if (!item) return null;

  return (
    <div
      className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
      onClick={onClose}
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
    >
      <div
        className="bg-white p-6 rounded-lg shadow-xl max-w-lg w-full"
        onClick={(e) => e.stopPropagation()}
      >
        <h3 id="modal-title" className="text-2xl font-bold mb-4">
          {item.masterInfo?.cltrNm}
        </h3>
        <p className="text-gray-700 mb-4">
          {item.masterInfo?.clnLdnmAdrs}
        </p>

        {/* 담당자 정보 */}
        {item.basicInfo && (
          <div className="mb-4 p-3 bg-gray-50 rounded border">
            <h4 className="text-lg font-semibold mb-2 text-gray-800">
              공고 담당자 정보
            </h4>
            <p className="text-sm text-gray-600">
              <span className="font-medium">담당부점:</span>{" "}
              {item.basicInfo.rsbyDept || "정보 없음"}
            </p>
            <p className="text-sm text-gray-600">
              <span className="font-medium">담당자:</span>{" "}
              {item.basicInfo.pscgNm || "정보 없음"}
            </p>
            <p className="text-sm text-gray-600">
              <span className="font-medium">연락처:</span>{" "}
              {item.basicInfo.pscgTpno || "정보 없음"}
            </p>
          </div>
        )}

        {item.fileList && item.fileList.length > 0 && (
          <div className="mb-4">
            <h4 className="text-lg font-semibold mb-2">첨부 파일</h4>
            <ul className="space-y-1 list-disc list-inside">
              {item.fileList.map((file, index) => (
                <li key={index} className="text-sm">
                  <span className="text-blue-600 hover:underline cursor-pointer">
                    {file.atchFileNm}
                  </span>
                </li>
              ))}
            </ul>
          </div>
        )}

        <h4 className="text-lg font-semibold mb-2">
          가격 변동 이력 ({item.priceHistory?.length || 0}건)
        </h4>

        {isLoading ? (
          <BarLoader color="#36d7b7" />
        ) : (
          <ul className="space-y-2 max-h-60 overflow-y-auto">
            {item.priceHistory?.map((history) => (
              <li
                key={history.cltrHstrNo}
                className="flex justify-between border-b pb-1"
              >
                <span className="text-gray-600">
                  {new Date(history.pbctClsDtm).toLocaleDateString()} 마감
                </span>
                <span className="font-bold">
                  {history.minBidPrc?.toLocaleString()}원
                </span>
              </li>
            ))}
          </ul>
        )}

        <button
          onClick={onClose}
          className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          aria-label="모달 닫기"
        >
          닫기
        </button>
      </div>
    </div>
  );
}

ItemDetailModal.propTypes = {
  item: PropTypes.shape({
    masterInfo: PropTypes.shape({
      cltrNm: PropTypes.string,
      clnLdnmAdrs: PropTypes.string,
    }),
    basicInfo: PropTypes.shape({
      rsbyDept: PropTypes.string,
      pscgNm: PropTypes.string,
      pscgTpno: PropTypes.string,
    }),
    fileList: PropTypes.arrayOf(
      PropTypes.shape({
        atchFileNm: PropTypes.string,
      })
    ),
    priceHistory: PropTypes.arrayOf(
      PropTypes.shape({
        cltrHstrNo: PropTypes.string.isRequired,
        pbctClsDtm: PropTypes.string,
        minBidPrc: PropTypes.number,
      })
    ),
  }),
  isLoading: PropTypes.bool,
  onClose: PropTypes.func.isRequired,
};

export default React.memo(ItemDetailModal);
