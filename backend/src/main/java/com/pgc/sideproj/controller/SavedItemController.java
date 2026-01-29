package com.pgc.sideproj.controller;

import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import com.pgc.sideproj.dto.db.UserDTO;
import com.pgc.sideproj.exception.custom.UserNotFoundException;
import com.pgc.sideproj.mapper.UserMapper;
import com.pgc.sideproj.service.SavedItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 찜하기 기능을 제공하는 컨트롤러입니다.
 * 
 * <p>사용자가 관심 있는 공매 물건을 찜 목록에 추가하거나 제거할 수 있습니다.
 * 모든 엔드포인트는 인증된 사용자만 접근 가능합니다.
 * 
 * @author sideproj
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/saved-items")
@RequiredArgsConstructor
public class SavedItemController {

    private final SavedItemService savedItemService;
    private final UserMapper userMapper;

    /**
     * 이메일을 기반으로 사용자 ID를 조회하는 헬퍼 메서드입니다.
     * 
     * @param email 사용자 이메일
     * @return 사용자 ID
     * @throws UserNotFoundException 해당 이메일의 사용자를 찾을 수 없는 경우
     */
    private Long getUserId(String email) {
        UserDTO user = userMapper.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));
        return user.getId();
    }

    /**
     * 공매 물건을 찜 목록에 추가합니다.
     * 
     * <p>이미 찜한 물건인 경우 DuplicateSavedItemException이 발생합니다.
     * 
     * @param email 인증된 사용자의 이메일 (JWT 토큰에서 자동 주입)
     * @param itemId 찜할 공매 물건의 ID (cltr_no)
     * @return 201 Created 상태 코드
     */
    @PostMapping("/{item_id}")
    public ResponseEntity<Void> addSavedItem(
            @AuthenticationPrincipal String email,
            @PathVariable("item_id") String itemId
    ) {
        Long userId = getUserId(email);
        savedItemService.addSavedItem(userId, itemId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 찜 목록에서 공매 물건을 제거합니다.
     * 
     * <p>해당 물건이 찜 목록에 없는 경우에도 정상적으로 처리됩니다.
     * 
     * @param email 인증된 사용자의 이메일 (JWT 토큰에서 자동 주입)
     * @param itemId 제거할 공매 물건의 ID (cltr_no)
     * @return 204 No Content 상태 코드
     */
    @DeleteMapping("/{item_id}")
    public ResponseEntity<Void> deleteSavedItem(
            @AuthenticationPrincipal String email,
            @PathVariable("item_id") String itemId
    ) {
        Long userId = getUserId(email);
        savedItemService.deleteSavedItem(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 현재 로그인한 사용자의 찜 목록을 조회합니다.
     * 
     * @param email 인증된 사용자의 이메일 (JWT 토큰에서 자동 주입)
     * @return 찜한 공매 물건 목록 (AuctionMasterDTO 리스트)
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
