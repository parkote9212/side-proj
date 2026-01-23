import PropTypes from "prop-types";
import React from "react";
import { Map, MapMarker, MarkerClusterer } from "react-kakao-maps-sdk";

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
