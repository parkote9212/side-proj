package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import com.pgc.sideproj.dto.db.SavedItemDTO;
import com.pgc.sideproj.exception.custom.DuplicateSavedItemException;
import com.pgc.sideproj.mapper.SavedItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 찜하기 기능을 제공하는 서비스입니다.
 * 
 * <p>사용자가 관심 있는 공매 물건을 찜 목록에 추가하거나 제거할 수 있는 비즈니스 로직을 처리합니다.
 * 
 * @author sideproj
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class SavedItemService {

    private final SavedItemMapper savedItemMapper;

    /**
     * 사용자의 찜 목록에 공매 물건을 추가합니다.
     * 
     * <p>이미 찜한 물건인 경우 DuplicateSavedItemException을 발생시킵니다.
     * 
     * @param userId 사용자 ID
     * @param itemId 공매 물건 ID (cltr_no)
     * @throws DuplicateSavedItemException 이미 찜한 물건인 경우
     */
    @Transactional
    public void addSavedItem(Long userId, String itemId) {
        if (savedItemMapper.findByUserIdAndItemId(userId, itemId).isPresent()) {
            throw new DuplicateSavedItemException(userId, itemId);
        }
        SavedItemDTO item = new SavedItemDTO();
        item.setUserId(userId);
        item.setItemId(itemId);
        savedItemMapper.save(item);
    }

    /**
     * 사용자의 찜 목록에서 공매 물건을 제거합니다.
     * 
     * <p>해당 물건이 찜 목록에 없는 경우에도 정상적으로 처리됩니다.
     * 
     * @param userId 사용자 ID
     * @param itemId 공매 물건 ID (cltr_no)
     */
    @Transactional
    public void deleteSavedItem(Long userId, String itemId) {
        if (savedItemMapper.findByUserIdAndItemId(userId, itemId).isEmpty()) {
            return;
        }

        savedItemMapper.delete(userId, itemId);
    }

    /**
     * 사용자의 찜 목록을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 찜한 공매 물건 목록 (AuctionMasterDTO 리스트)
     */
    @Transactional(readOnly = true)
    public List<AuctionMasterDTO> getMySavedItems(Long userId) {
        return savedItemMapper.findItemsByUserId(userId);
    }
}
