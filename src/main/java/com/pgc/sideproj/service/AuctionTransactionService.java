package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.db.AuctionHistoryDTO;
import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import com.pgc.sideproj.dto.kakao.KakaoAddressResponseDTO;
import com.pgc.sideproj.dto.onbid.OnbidItemDTO;
import com.pgc.sideproj.mapper.AuctionItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionTransactionService {

    private final DataCleansingService dataCleansingService;
    private final KakaoMapService kakaoMapService;
    private final AuctionItemMapper auctionItemMapper;

    @Transactional
    public void processPageItems(List<OnbidItemDTO> items) {
        log.info("{}개 항목의 트랜잭션을 시작합니다.", items.size());

        for (OnbidItemDTO item : items) {
            processItem(item);
        }
        
        log.info("{}개 항목의 트랜잭션을 커밋합니다.", items.size());
    }

    private void processItem(OnbidItemDTO item) {
        try {
            AuctionMasterDTO master = dataCleansingService.createMasterFrom(item);
            AuctionHistoryDTO history = dataCleansingService.createHistoryFrom(item);

            processGeocoding(master);

            auctionItemMapper.upsertMaster(master);
            auctionItemMapper.upsertHistory(history);

        } catch (Exception e) {
            log.error("아이템(cltrNo:{}) 처리 중 오류 발생. 이 페이지의 트랜잭션을 롤백합니다.", item.getCltrNo(), e);
            throw new RuntimeException("아이템 처리 실패: " + item.getCltrNo(), e);
        }
    }

    private void processGeocoding(AuctionMasterDTO master) {
        String addressToGeocode = master.getClnLdnmAdrs();

        if (addressToGeocode == null || addressToGeocode.isBlank()) {
            addressToGeocode = master.getClnNmrdAdrs();
        }

        if (addressToGeocode != null && !addressToGeocode.isBlank()) {
            KakaoAddressResponseDTO.DocumentDTO coords = kakaoMapService.getCoordinates(addressToGeocode);

            if (coords != null) {
                master.setLatitude(coords.getLatitude());
                master.setLongitude(coords.getLongitude());
            } else {
                log.warn("좌표 획득 실패 (주소: {})", addressToGeocode);
            }
        } else {
            log.warn("유효한 주소가 없어 Geocoding을 스킵합니다");
        }
    }
}