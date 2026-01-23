package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.onbid.OnbidApiResponseDTO;
import com.pgc.sideproj.dto.onbid.OnbidItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 온비드 공매 데이터 배치 수집 서비스
 * - 매일 새벽 1시 자동 실행
 * - ShedLock으로 분산 환경에서 중복 실행 방지
 * - 부분 실패 허용 (한 항목 실패 시 다른 항목 계속 처리)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionBatchService {

    private final OnbidApiService onbidApiService;
    private final AuctionTransactionService auctionTransactionService;

    /**
     * 매일 01:00에 온비드 데이터를 수집합니다.
     * ShedLock을 사용하여 분산 환경에서도 중복 실행 방지
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @SchedulerLock(
        name = "onbidBatchRun",
        lockAtLeastFor = "PT5M",
        lockAtMostFor = "PT30M"
    )
    public void scheduledBatchRun() {
        log.info("===== 온비드 배치 작업 시작 (스케줄러) =====");
        long startTime = System.currentTimeMillis();
        
        try {
            fetchAndSaveOnbidData();
            long duration = System.currentTimeMillis() - startTime;
            log.info("===== 온비드 배치 작업 완료 ({}ms) =====", duration);
        } catch (Exception e) {
            log.error("===== 온비드 배치 작업 실패 =====", e);
        }
    }

    /**
     * 수동 트리거용 배치 (테스트/관리자용)
     */
    public void fetchAndSaveOnbidData() {
        log.info("Onbid 전체 데이터 수집 배치를 시작합니다.");
        processBatchData();
        log.info("배치 작업 완료.");
    }

    /**
     * 온비드 API에서 모든 페이지의 데이터를 수집합니다.
     * 개선: 부분 실패 처리 추가
     */
    private void processBatchData() {
        int pageNo = 1;
        int totalCount = 0;
        final int numOfRows = 100;
        int processedCount = 0;
        int errorCount = 0;

        while (true) {
            log.info("Onbid API 요청 ({} 페이지 / {}개)", pageNo, numOfRows);
            
            OnbidApiResponseDTO response = fetchApiResponse(pageNo, numOfRows);
            if (response == null) {
                log.warn("API 응답이 null입니다. 배치를 중단합니다.");
                break;
            }

            // 첫 번째 페이지에서 총 개수 확인
            if (pageNo == 1) {
                totalCount = response.getBody().getTotalCount();
                if (totalCount == 0) {
                    log.info("수집할 데이터가 없습니다.");
                    return;
                }
                log.info("총 수집할 물건 수: {}", totalCount);
            }

            List<OnbidItemDTO> items = response.getBody().getItems();
            if (items.isEmpty()) {
                log.info("{}페이지부터 데이터가 없습니다. 배치를 종료합니다.", pageNo);
                break;
            }

            // 개선: 페이지 단위 부분 실패 처리
            int pageProcessed = processPageWithPartialRetry(items, pageNo);
            processedCount += pageProcessed;
            errorCount += (items.size() - pageProcessed);

            pageNo++;
            
            // Rate Limiting (1초 대기)
            if (!waitForNextPage()) {
                break;
            }
            
            // 모든 데이터를 수집했으면 종료
            if ((pageNo - 1) * numOfRows >= totalCount) {
                log.info("모든 데이터 수집 완료");
                break;
            }
        }
        
        log.info("배치 결과 - 총 처리: {}건, 성공: {}건, 실패: {}건",
                processedCount + errorCount, processedCount, errorCount);
    }

    /**
     * 개선: 한 항목 실패 시 다른 항목까지 롤백되지 않도록 처리
     */
    private int processPageWithPartialRetry(List<OnbidItemDTO> items, int pageNo) {
        int successCount = 0;
        
        for (OnbidItemDTO item : items) {
            try {
                auctionTransactionService.processSingleItem(item);
                successCount++;
            } catch (Exception e) {
                log.error("항목 처리 실패 (cltrNo: {}, 페이지: {}): {}",
                        item.getCltrNo(), pageNo, e.getMessage());
                // 개별 항목의 실패는 계속 진행
            }
        }
        
        log.info("{}페이지 처리 완료 ({}/{}개 성공)", pageNo, successCount, items.size());
        return successCount;
    }

    private OnbidApiResponseDTO fetchApiResponse(int pageNo, int numOfRows) {
        try {
            OnbidApiResponseDTO response = onbidApiService.fetchOnbidData(pageNo, numOfRows);
            if (response == null || response.getBody() == null || response.getBody().getItems() == null) {
                log.warn("{} 페이지에서 유효한 응답(body)을 받지 못했습니다.", pageNo);
                return null;
            }
            return response;
        } catch (Exception e) {
            log.error("Onbid API 호출 실패 ({} 페이지): {}", pageNo, e.getMessage());
            return null;
        }
    }

    private boolean waitForNextPage() {
        try {
            Thread.sleep(1000);
            return true;
        } catch (InterruptedException e) {
            log.warn("스레드 인터럽트 발생. 배치를 중단합니다.");
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
