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

/**
 * 공매 물건 데이터 처리 트랜잭션을 관리하는 서비스입니다.
 * 
 * <p>온비드 API에서 받은 데이터를 정제하고, 지오코딩을 수행한 후 데이터베이스에 저장합니다.
 * 
 * @author sideproj
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionTransactionService {

    private final DataCleansingService dataCleansingService;
    private final KakaoMapService kakaoMapService;
    private final AuctionItemMapper auctionItemMapper;

    /**
     * 여러 공매 물건을 한 번에 처리합니다.
     * 
     * <p>페이지 단위로 여러 항목을 처리하며, 하나의 항목이라도 실패하면 전체 트랜잭션이 롤백됩니다.
     * 
     * @param items 처리할 공매 물건 목록
     * @throws RuntimeException 항목 처리 중 오류가 발생한 경우
     */
    @Transactional
    public void processPageItems(List<OnbidItemDTO> items) {
        log.info("{}개 항목의 트랜잭션을 시작합니다.", items.size());

        for (OnbidItemDTO item : items) {
            processItem(item);
        }
        
        log.info("{}개 항목의 트랜잭션을 커밋합니다.", items.size());
    }

    /**
     * 단일 공매 물건을 처리합니다.
     * 
     * <p>데이터 정제, 지오코딩, 데이터베이스 저장을 수행합니다.
     * 
     * @param item 처리할 공매 물건 DTO
     * @throws RuntimeException 항목 처리 중 오류가 발생한 경우
     */
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

    /**
     * 공매 물건의 주소를 지오코딩하여 좌표를 설정합니다.
     * 
     * <p>도로명 주소를 우선 사용하고, 없으면 지번 주소를 사용합니다.
     * 카카오 맵 API를 사용하여 주소를 좌표로 변환합니다.
     * 
     * @param master 지오코딩할 공매 물건 마스터 정보
     */
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
            log.warn("유효한 주소가 없어 지오코딩을 스킵합니다");
        }
    }

    /**
     * 단일 공매 물건을 독립적인 트랜잭션으로 처리합니다.
     * 
     * <p>배치 작업에서 개별 항목 단위로 처리할 때 사용됩니다.
     * 하나의 항목 실패가 다른 항목에 영향을 주지 않도록 독립적인 트랜잭션으로 처리합니다.
     * 
     * @param item 처리할 공매 물건 DTO
     */
    @Transactional
    public void processSingleItem(OnbidItemDTO item) {
        AuctionMasterDTO master = dataCleansingService.createMasterFrom(item);
        AuctionHistoryDTO history = dataCleansingService.createHistoryFrom(item);
        processGeocoding(master);
        auctionItemMapper.upsertMaster(master);
        auctionItemMapper.upsertHistory(history);
    }
}