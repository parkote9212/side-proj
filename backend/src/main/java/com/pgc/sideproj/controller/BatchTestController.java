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
 * 
 * <p>이 컨트롤러는 개발 및 테스트 환경에서 배치 작업을 수동으로 실행하기 위해 제공됩니다.
 * 프로덕션 환경에서는 {@code app.batch.manual-trigger.enabled} 속성을 false로 설정하여 비활성화할 수 있습니다.
 * 
 * @author sideproj
 * @since 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.batch.manual-trigger.enabled", havingValue = "true", matchIfMissing = false)
public class BatchTestController {

    private final AuctionBatchService auctionBatchService;

    /**
     * 온비드 배치 작업을 수동으로 실행합니다.
     * 
     * <p>이 엔드포인트는 관리자가 수동으로 배치 작업을 트리거할 때 사용됩니다.
     * 배치 작업은 온비드 API에서 데이터를 가져와 데이터베이스에 저장합니다.
     * 
     * @return 배치 실행 결과 메시지
     *         - 성공 시: "Batch executed successfully"
     *         - 실패 시: 에러 메시지와 함께 500 상태 코드
     */
    @PostMapping("/api/admin/batch/run")
    public ResponseEntity<String> runBatch() {
        log.info("=== 수동 배치 실행 요청됨 ===");
        try {
            log.info("배치 서비스 실행 시작 중...");
            auctionBatchService.fetchAndSaveOnbidData();
            log.info("수동 배치 실행이 성공적으로 완료되었습니다");
            return ResponseEntity.ok("배치 실행이 성공했습니다");
        } catch (Exception e) {
            log.error("수동 배치 실행 실패 - 오류: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("배치 실행 실패: " + e.getMessage());
        }
    }
}
