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

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuctionBatchService auctionBatchService;
    private final UserService userService;

    /**
     * [POST] /api/v1/admin/batch/run : 수동 배치 실행
     * (SecurityConfig에서 ROLE_ADMIN만 접근 가능하도록 설정됨)
     */

    @PostMapping("/batch/run")
    public ResponseEntity<String> runBatch(){
        try {
                log.info("관리자에 의해 수동 배치가 요청되었습니다.");
                auctionBatchService.fetchAndSaveOnbidData();
                log.info("수동 배치 실행 완료.");
                return ResponseEntity.ok("배치 실해엥 성공했습니다.");
        }catch (Exception e){
            log.error("수동 배치 실행 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body("배치 실행에 실패했습니다. 로그를 확인하세요");
        }
    }
    /**
     * [GET] /api/v1/admin/users : 모든 회원 목록 조회
     * (SecurityConfig에서 ROLE_ADMIN만 접근 가능)
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> userList = userService.getAllUsers();
        return ResponseEntity.ok(userList);
    }


}
