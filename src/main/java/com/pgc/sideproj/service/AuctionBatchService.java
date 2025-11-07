package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.db.AuctionHistoryDTO;
import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import com.pgc.sideproj.dto.kakao.KakaoAddressResponseDTO;
import com.pgc.sideproj.dto.onbid.OnbidApiResponseDTO;
import com.pgc.sideproj.dto.onbid.OnbidItemDTO;
import com.pgc.sideproj.mapper.AuctionItemMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionBatchService {

    private static final Logger log = LoggerFactory.getLogger(AuctionBatchService.class);

    private final OnbidApiService onbidApiService;
    private final DataCleansingService dataCleansingService;
    private final KakaoMapService kakaoMapService;
    private final AuctionItemMapper auctionItemMapper;

    @Scheduled(cron = "0 0 1 * * ?")
    public void fetchAndSaveOnbidData(){
        int pageNo = 1;
        int totalCount = 0;
        final int numOfRows = 100;

        log.info("Onbid 전체 데이터 수집 배치를 시작합니다.");

        do {
            log.info("Onbid API 요청 ({} 페이지 / {}개)", pageNo, numOfRows);
            OnbidApiResponseDTO response;
            try {
                response = onbidApiService.fetchOnbidData(pageNo, numOfRows);
            } catch (Exception e) {
                log.error("Onbid API 호출 중 치명적 오류 발생 ({} 페이지). 배치를 중단합니다.", pageNo, e);
                break; // API 호출 자체에 문제 발생 시 중단
            }

            // 2. 방어 코드: 응답이 비정상일 경우
            if (response == null || response.getBody() == null || response.getBody().getItems() == null) {
                log.warn("{} 페이지에서 유효한 응답(body)을 받지 못했습니다. 배치를 중단합니다.", pageNo);
                break;
            }

            // 3. 총 개수 업데이트 (첫 페이지에서만 실행)
            if (pageNo == 1) {
                totalCount = response.getBody().getTotalCount();
                if (totalCount == 0) {
                    log.info("수집할 데이터가 없습니다. 배치를 종료합니다.");
                    break;
                }
                log.info("총 데이터 수: {}", totalCount);
            }

            List<OnbidItemDTO> items = response.getBody().getItems();
            if (items.isEmpty()) {
                log.warn("{} 페이지에 데이터가 없습니다. 배치를 종료합니다.", pageNo);
                break;
            }

            // 4. (핵심) 1페이지(100개) 데이터 처리 (트랜잭션 단위)
            try {
                processPageItems(items);
                log.info("{} 페이지 ({}개 항목) 처리 완료.", pageNo, items.size());
            } catch (Exception e) {
                // processPageItems 내부에서 롤백이 발생하고 예외가 re-throw 됨
                log.error("{} 페이지 처리 중 트랜잭션 오류 발생. 롤백되었습니다. 배치를 중단합니다.", pageNo, e);
                break; // 한 페이지라도 실패하면 전체 배치 중단
            }

            // 5. 페이지네이션
            pageNo++;

            // 6. Rate Limiting (API 호출 사이에 1초 휴식)
            // 다음 페이지를 호출하기 전에 1초 대기
            if ((pageNo - 1) * numOfRows < totalCount) {
                try {
                    log.info("다음 페이지 요청 전 1초 대기 (Rate Limiting)...");
                    Thread.sleep(1000); // 1초
                } catch (InterruptedException e) {
                    log.warn("Thread sleep 중 인터럽트 발생. 배치를 중단합니다.");
                    Thread.currentThread().interrupt(); // 인터럽트 상태 복원
                    break;
                }
            }

        } while ((pageNo - 1) * numOfRows < totalCount); // (이전 페이지까지 처리한 수 < 총 개수) 동안 반복

        log.info("Onbid 전체 데이터 수집 배치를 완료했습니다. (총 {} 페이지 처리 시도)", pageNo - 1);
    }

    /**
     * 1페이지(100개) 분량의 아이템 리스트를 하나의 트랜잭션으로 처리합니다.
     * @param items Onbid API에서 가져온 100개(1페이지) 아이템 리스트
     */
    @Transactional
    public void processPageItems(List<OnbidItemDTO> items) {
        log.info("{}개 항목의 트랜잭션을 시작합니다.", items.size());

        for (OnbidItemDTO item : items) {
            // 7. 개별 아이템 처리 (for 루프 내부)
            try {
                // 7-1. 데이터 정제 (API DTO -> DB DTO)
                AuctionMasterDTO master = dataCleansingService.createMasterFrom(item);
                AuctionHistoryDTO history = dataCleansingService.createHistoryFrom(item);

                // 7-2. Geocoding (정제된 주소가 있을 때만)
                String addressToGeocode = master.getClnLdnmAdrs();

                // (개선) 지번주소가 없으면 도로명주소로 시도
                if (addressToGeocode == null || addressToGeocode.isBlank()) {
                    addressToGeocode = master.getClnNmrdAdrs();
                }

                if (addressToGeocode != null && !addressToGeocode.isBlank()) {
                    // 카카오 API 호출
                    KakaoAddressResponseDTO.DocumentDTO coords = kakaoMapService.getCoordinates(addressToGeocode);

                    // 7-3. 좌표 DTO에 반영
                    if (coords != null) {
                        master.setLatitude(coords.getLatitude());
                        master.setLongitude(coords.getLongitude());
                    } else {
                        log.warn("좌표 획득 실패 (주소: {}): {}", addressToGeocode, item.getCltrNo());
                    }
                } else {
                    log.warn("유효한 주소가 없어 Geocoding을 스킵합니다: {}", item.getCltrNo());
                }

                // 7-4. DB 저장 (UPSERT)
                auctionItemMapper.upsertMaster(master);
                auctionItemMapper.upsertHistory(history);

            } catch (Exception e) {
                // 한 아이템이라도 처리 실패 시, 이 트랜잭션(100개) 전체를 롤백시키기 위해 예외를 다시 던짐
                log.error("아이템(cltrNo:{}) 처리 중 오류 발생. 이 페이지의 트랜잭션을 롤백합니다.", item.getCltrNo(), e);
                throw e; // @Transactional이 예외를 감지하고 롤백 수행
            }
        }
        log.info("{}개 항목의 트랜잭션을 커밋합니다.", items.size());
    }
}
