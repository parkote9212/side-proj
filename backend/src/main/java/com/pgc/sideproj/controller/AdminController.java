package com.pgc.sideproj.controller;

import com.pgc.sideproj.dto.response.UserResponse;
import com.pgc.sideproj.service.AuctionBatchService;
import com.pgc.sideproj.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 관리자 전용 API 컨트롤러입니다.
 * 
 * <p>이 컨트롤러의 모든 엔드포인트는 SecurityConfig에서 ROLE_ADMIN 권한이 필요하도록 설정되어 있습니다.
 * 
 * @author sideproj
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuctionBatchService auctionBatchService;
    private final UserService userService;

    /**
     * 온비드 배치 작업을 수동으로 실행합니다.
     * 
     * <p>관리자가 수동으로 온비드 API에서 데이터를 수집하는 배치 작업을 트리거할 수 있습니다.
     * 이 작업은 시간이 오래 걸릴 수 있으므로 비동기로 처리하는 것을 권장합니다.
     * 
     * @return 배치 실행 결과 메시지
     *         - 성공 시: "배치 실행에 성공했습니다."
     *         - 실패 시: 에러 메시지와 함께 500 상태 코드
     */
    @PostMapping("/batch/run")
    public ResponseEntity<String> runBatch(){
        try {
            log.info("관리자에 의해 수동 배치가 요청되었습니다.");
            auctionBatchService.fetchAndSaveOnbidData();
            log.info("수동 배치 실행 완료.");
            return ResponseEntity.ok("배치 실행에 성공했습니다.");
        }catch (Exception e){
            log.error("수동 배치 실행 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body("배치 실행에 실패했습니다. 로그를 확인하세요");
        }
    }

    /**
     * 시스템에 등록된 모든 회원 목록을 조회합니다.
     * 
     * <p>관리자만 접근 가능한 엔드포인트로, 모든 사용자의 정보를 조회할 수 있습니다.
     * 비밀번호는 응답에 포함되지 않습니다.
     * 
     * @return 모든 회원 정보 목록 (UserResponse 리스트)
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> userList = userService.getAllUsers();
        return ResponseEntity.ok(userList);
    }
}
