package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import com.pgc.sideproj.dto.db.SavedItemDTO;
import com.pgc.sideproj.mapper.SavedItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedItemService {

    private final SavedItemMapper savedItemMapper;

    @Transactional
    public void addSavedItem(Long userId, String itemId) {
        if (savedItemMapper.findByUserIdAndItemId(userId, itemId).isPresent()) {
            throw new RuntimeException("이미 찜 목록에 추가된 물건입니다.");
        }
        SavedItemDTO item = new SavedItemDTO();
        item.setUserId(userId);
        item.setItemId(itemId);
        savedItemMapper.save(item);

    }

    @Transactional
    public void deleteSavedItem(Long userId, String itemId) { // 파라미터 변경
        if (savedItemMapper.findByUserIdAndItemId(userId, itemId).isEmpty()) {
            return;
        }

        savedItemMapper.delete(userId, itemId);
    }

    @Transactional(readOnly = true)
    public List<AuctionMasterDTO> getMySavedItems(Long userId) {
        return savedItemMapper.findItemsByUserId(userId);
    }

}
