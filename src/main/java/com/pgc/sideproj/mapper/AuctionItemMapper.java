package com.pgc.sideproj.mapper;

import com.pgc.sideproj.dto.db.AuctionHistoryDTO;
import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuctionItemMapper {

    void upsertMaster(AuctionMasterDTO master);
    void upsertHistory(AuctionHistoryDTO history);

}
