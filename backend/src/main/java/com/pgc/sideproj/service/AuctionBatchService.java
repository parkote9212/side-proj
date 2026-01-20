/*
package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.onbid.OnbidApiResponseDTO;
import com.pgc.sideproj.dto.onbid.OnbidItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionBatchService {

    private final OnbidApiService onbidApiService;
    private final AuctionTransactionService auctionTransactionService;

    // ====== 온비드 API 기능 임시 비활성화 ======
    // TODO: 온비드 API 사용 시 주석 해제
    */
/*
    @Scheduled(cron = "0 0 1 * * ?")
    @SchedulerLock(name = "onbidBatchRun", lockAtLeastFor = "PT5M", lockAtMostFor = "PT30M")
    public void scheduledBatchRun() {
        log.info("스케줄러에 의해 배치가 실행됩니다.");
        fetchAndSaveOnbidData();
    }

    // @SchedulerLock(name = "onbidManualBatchRun", lockAtLeastFor = "PT1M", lockAtMostFor = "PT30M")
    public void fetchAndSaveOnbidData(){
        log.info("Onbid 전체 데이터 수집 배치를 시작합니다.");
        processBatchData();
        log.info("배치 작업 완료.");
    }
    *//*


    */
/*
    private void processBatchData() {
        int pageNo = 1;
        int totalCount = 0;
        final int numOfRows = 100;
        boolean shouldContinue = true;

        while (shouldContinue) {
            log.info("Onbid API 요청 ({} 페이지 / {}개)", pageNo, numOfRows);
            
            OnbidApiResponseDTO response = fetchApiResponse(pageNo, numOfRows);
            if (response == null) {
                break;
            }

            if (pageNo == 1) {
                totalCount = initializeTotalCount(response);
                if (totalCount == 0) {
                    break;
                }
            }

            List<OnbidItemDTO> items = response.getBody().getItems();
            if (items.isEmpty()) {
                log.warn("{} 페이지에 데이터가 없습니다. 배치를 종료합니다.", pageNo);
                break;
            }

            if (!processPageWithTransaction(items, pageNo)) {
                break;
            }

            pageNo++;
            shouldContinue = hasMorePages(pageNo, numOfRows, totalCount) && waitForNextPage();
        }

        log.info("Onbid 전체 데이터 수집 배치를 완료했습니다. (총 {} 페이지 처리 시도)", pageNo - 1);
    }

    private OnbidApiResponseDTO fetchApiResponse(int pageNo, int numOfRows) {
        try {
            OnbidApiResponseDTO response = onbidApiService.fetchOnbidData(pageNo, numOfRows);
            if (response == null || response.getBody() == null || response.getBody().getItems() == null) {
                log.warn("{} 페이지에서 유효한 응답(body)을 받지 못했습니다. 배치를 중단합니다.", pageNo);
                return null;
            }
            return response;
        } catch (Exception e) {
            log.error("Onbid API 호출 중 치명적 오류 발생 ({} 페이지). 배치를 중단합니다.", pageNo, e);
            return null;
        }
    }

    private int initializeTotalCount(OnbidApiResponseDTO response) {
        int totalCount = response.getBody().getTotalCount();
        if (totalCount == 0) {
            log.info("수집할 데이터가 없습니다. 배치를 종료합니다.");
        } else {
            log.info("총 데이터 수: {}", totalCount);
        }
        return totalCount;
    }

    private boolean processPageWithTransaction(List<OnbidItemDTO> items, int pageNo) {
        try {
            auctionTransactionService.processPageItems(items);
            log.info("{} 페이지 ({}개 항목) 처리 완료.", pageNo, items.size());
            return true;
        } catch (Exception e) {
            log.error("{} 페이지 처리 중 트랜잭션 오류 발생. 롤백되었습니다. 배치를 중단합니다.", pageNo, e);
            return false;
        }
    }

    private boolean hasMorePages(int pageNo, int numOfRows, int totalCount) {
        return (pageNo - 1) * numOfRows < totalCount;
    }

    private boolean waitForNextPage() {
        try {
            log.info("다음 페이지 요청 전 1초 대기 (Rate Limiting)...");
            Thread.sleep(1000);
            return true;
        } catch (InterruptedException e) {
            log.warn("Thread sleep 중 인터럽트 발생. 배치를 중단합니다.", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }
    *//*


}
*/
