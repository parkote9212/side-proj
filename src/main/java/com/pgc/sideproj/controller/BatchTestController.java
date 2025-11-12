package com.pgc.sideproj.controller;

import com.pgc.sideproj.service.AuctionBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Phase 1 배치 작업을 수동으로 트리거하기 위한 임시 테스트 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.batch.manual-trigger.enabled", havingValue = "true", matchIfMissing = false)
public class BatchTestController {

    private final AuctionBatchService auctionBatchService;

    /**
     * http://localhost:8080/test-batch-run URL로 GET 요청 시
     * Onbid 배치 파이프라인을 실행합니다.
     *
     * @return 배치 시작 확인 메시지
     */

    @PostMapping("/api/admin/batch/run")
    public ResponseEntity<String> runBatch() {
        log.info("=== Manual batch execution requested ===");
        try {
            log.info("Starting batch service execution...");
            auctionBatchService.fetchAndSaveOnbidData();
            log.info("Manual batch execution completed successfully");
            return ResponseEntity.ok("Batch executed successfully");
        } catch (Exception e) {
            log.error("Manual batch execution failed with error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Batch execution failed: " + e.getMessage());
        }
    }
}

