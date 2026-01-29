import PropTypes from "prop-types";
import React from "react";
import { Map, MapMarker, MarkerClusterer } from "react-kakao-maps-sdk";

/**
 * 경매 물건 지도 컴포넌트
 * 
 * 카카오맵을 사용하여 물건들의 위치를 마커로 표시합니다.
 * 
 * @component
 * @param {Object} props - 컴포넌트 props
 * @param {Array} props.items - 물건 목록
 * @param {Object} props.mapCenter - 지도 중심 좌표
 * @param {Function} props.onItemClick - 마커 클릭 핸들러
 * @returns {JSX.Element} 지도 컴포넌트
 */
function ItemMap({ items, mapCenter, onItemClick }) {
  return (
    <div className="w-2/3 max-w-[1280px] shadow rounded-lg overflow-hidden">
      <Map
        center={mapCenter}
        style={{ width: "100%", height: "100%" }}
        level={7}
        aria-label="경매 물건 지도"
      >
        <MarkerClusterer averageCenter={true}>
          {items.map((item) => (
            <MapMarker
              key={item.cltrNo}
              position={{ lat: item.latitude, lng: item.longitude }}
              clickable={true}
              onClick={() => onItemClick(item.cltrNo)}
            />
          ))}
        </MarkerClusterer>
      </Map>
    </div>
  );
}

ItemMap.propTypes = {
  items: PropTypes.arrayOf(
    PropTypes.shape({
      cltrNo: PropTypes.string.isRequired,
      latitude: PropTypes.number.isRequired,
      longitude: PropTypes.number.isRequired,
    })
  ).isRequired,
  mapCenter: PropTypes.shape({
    lat: PropTypes.number.isRequired,
    lng: PropTypes.number.isRequired,
  }).isRequired,
  onItemClick: PropTypes.func.isRequired,
};

export default React.memo(ItemMap);
