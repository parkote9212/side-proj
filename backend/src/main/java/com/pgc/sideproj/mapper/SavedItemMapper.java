package com.pgc.sideproj.mapper;

import com.pgc.sideproj.dto.db.SavedItemDTO;
import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SavedItemMapper {

    void save(SavedItemDTO item);

    /**
     * @Param 이름 변경: cltrNo -> itemId
     */
    void delete(@Param("userId") Long userId, @Param("itemId") String itemId);

    /**
     * @Param 이름 변경: cltrNo -> itemId
     */
    Optional<SavedItemDTO> findByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") String itemId);

    List<AuctionMasterDTO> findItemsByUserId(Long userId);
}