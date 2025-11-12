package com.pgc.sideproj.controller;

import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import com.pgc.sideproj.dto.db.UserDTO;
import com.pgc.sideproj.mapper.UserMapper;
import com.pgc.sideproj.service.SavedItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/saved-items")
@RequiredArgsConstructor
public class SavedItemController {

    private final SavedItemService savedItemService;
    private final UserMapper userMapper;

    // ... getUserId 헬퍼 메서드는 변경 없음 ...
    private Long getUserId(String email) {
        UserDTO user = userMapper.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("인증된 사용자 정보를 찾을 수 없습니다."));
        return user.getId();
    }

    /**
     * [POST] /api/v1/saved-items/{item_id} : 물건 찜하기
     */
    @PostMapping("/{item_id}")
    public ResponseEntity<Void> addSavedItem(
            @AuthenticationPrincipal String email,
            @PathVariable("item_id") String itemId // @PathVariable 이름 변경 및 변수명 변경
    ) {
        Long userId = getUserId(email);
        savedItemService.addSavedItem(userId, itemId); // 서비스 호출 파라미터 변경
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * [DELETE] /api/v1/saved-items/{item_id} : 찜 취소
     */
    @DeleteMapping("/{item_id}")
    public ResponseEntity<Void> deleteSavedItem(
            @AuthenticationPrincipal String email,
            @PathVariable("item_id") String itemId // @PathVariable 이름 변경 및 변수명 변경
    ) {
        Long userId = getUserId(email);
        savedItemService.deleteSavedItem(userId, itemId); // 서비스 호출 파라미터 변경
        return ResponseEntity.noContent().build();
    }

    /**
     * [GET] /api/v1/saved-items : 내 찜 목록 조회 (변경 없음)
     */
    @GetMapping
    public ResponseEntity<List<AuctionMasterDTO>> getMySavedItems(
            @AuthenticationPrincipal String email
    ) {
        Long userId = getUserId(email);
        List<AuctionMasterDTO> savedItems = savedItemService.getMySavedItems(userId);
        return ResponseEntity.ok(savedItems);
    }
}