package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.db.AuctionHistoryDTO;
import com.pgc.sideproj.dto.db.AuctionMasterDTO;
import com.pgc.sideproj.dto.onbid.OnbidBasicInfoDTO;
import com.pgc.sideproj.dto.onbid.OnbidBasicInfoResponseDTO;
import com.pgc.sideproj.dto.onbid.OnbidFileInfoResponseDTO;
import com.pgc.sideproj.dto.response.AuctionItemDetailDTO;
import com.pgc.sideproj.dto.response.AuctionItemSummaryDTO;
import com.pgc.sideproj.dto.response.BasicInfoResponseDTO;
import com.pgc.sideproj.dto.response.PageResponseDTO;
import com.pgc.sideproj.exception.custom.ResourceNotFoundException;
import com.pgc.sideproj.mapper.AuctionItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 공매 물건 조회 관련 비즈니스 로직을 처리하는 서비스입니다.
 * 
 * <p>공매 물건 목록 조회 및 상세 정보 조회 기능을 제공합니다.
 * 목록 조회는 Full-Text Search(FTS)를 지원하며 페이지네이션이 적용됩니다.
 * 상세 정보 조회 시 온비드 API를 호출하여 추가 정보를 가져옵니다.
 * 
 * @author sideproj
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionItemService {

    private final AuctionItemMapper auctionItemMapper;
   private final OnbidApiService onbidApiService;

    /**
     * FTS 검색 및 페이지네이션을 적용하여 물건 목록을 조회합니다.
     *
     * @param keyword 검색어 (FTS 대상)
     * @param page    현재 페이지 (1부터 시작)
     * @param size    페이지 크기
     * @return 페이지네이션 결과 DTO (PageResponseDTO)
     */
    public PageResponseDTO<AuctionItemSummaryDTO> getItems(String keyword, String region, int page, int size) {
        log.info("물건 검색 중 - 검색어: {}, 지역: {}, 페이지: {}, 크기: {}",
                keyword, region, page, size);

        try {
            // 1. offset 계산
            int offset = (page - 1) * size;

            // 2. DB에서 총 개수 조회 (FTS 검색어, region 포함)
            int totalCount = auctionItemMapper.countItems(keyword, region);

            if (totalCount == 0) {
                log.warn("검색 결과 없음 - 검색어: {}, 지역: {}", keyword, region);
            }

            // 3. DB에서 데이터 목록 조회 (FTS 검색어, region, 페이지네이션 포함)
            List<AuctionItemSummaryDTO> items = auctionItemMapper.findItems(keyword, region, offset, size);

            log.debug("총 {}개 중 {}개 항목 조회됨", totalCount, items.size());

            // 4. PageResponseDTO로 래핑하여 반환
            return new PageResponseDTO<>(items, page, size, totalCount);
        } catch (Exception e) {
            log.error("물건 검색 중 오류 발생 - 검색어: {}, 지역: {}",
                    keyword, region, e);
            throw e;
        }
    }

    /**
     * 공매 물건의 상세 정보를 조회합니다.
     * 
     * <p>데이터베이스에서 기본 정보와 가격 이력을 조회하고,
     * 온비드 API를 호출하여 담당자 정보와 첨부 파일 목록을 가져옵니다.
     * 
     * @param cltrNo 공매 물건 번호 (cltr_no)
     * @return 공매 물건 상세 정보 DTO (기본 정보, 가격 이력, 담당자 정보, 첨부 파일 목록 포함)
     * @throws ResourceNotFoundException 해당 물건을 찾을 수 없는 경우
     */
    public AuctionItemDetailDTO getItemDetail(String cltrNo) {
        log.info("물건 상세 정보 조회 중 - 물건번호: {}", cltrNo);

        try {
            AuctionMasterDTO master = auctionItemMapper.findMasterByCltrNo(cltrNo)
                    .orElseThrow(() -> new ResourceNotFoundException("AuctionMaster", "cltrNo", cltrNo));

        List<AuctionHistoryDTO> history = auctionItemMapper.findHistoryByCltrNo(cltrNo);

        OnbidBasicInfoDTO xmlBasicInfo = null;
        OnbidBasicInfoResponseDTO basicResponse = onbidApiService.fetchBasicInfoDetail(master.getPlnmNo(), master.getPbctNo());
        if (basicResponse != null && basicResponse.getBody() != null){
            xmlBasicInfo = basicResponse.getBody().getItem();
        }

        List<OnbidFileInfoResponseDTO.OnbidFileInfoDTO> fileList = Collections.emptyList();
        OnbidFileInfoResponseDTO fileResponse = onbidApiService.fetchFileInfoDetail(master.getPlnmNo(), master.getPbctNo());
        if (fileResponse != null && fileResponse.getBody() != null && fileResponse.getBody().getFiles() != null) {
            fileList = fileResponse.getBody().getFiles();
        }

        BasicInfoResponseDTO responseBasicInfo = null;
        if (xmlBasicInfo != null) {
            responseBasicInfo = BasicInfoResponseDTO.builder()
                    .plnmNm(xmlBasicInfo.getPlnmNm())
                    .rsbyDept(xmlBasicInfo.getRsbyDept())
                    .pscgNm(xmlBasicInfo.getPscgNm())
                    .pscgTpno(xmlBasicInfo.getPscgTpno())
                    .pscgEmalAdrs(xmlBasicInfo.getPscgEmalAdrs())
                    .build();
        }

            AuctionItemDetailDTO detail = AuctionItemDetailDTO.builder()
                    .masterInfo(master)
                    .priceHistory(history)
                    .basicInfo(responseBasicInfo)
                    .fileList(fileList)
                    .build();

            log.debug("물건 상세 정보 조회 성공 - 물건번호: {}", cltrNo);
            return detail;
        } catch (Exception e) {
            log.error("물건 상세 정보 조회 중 오류 발생 - 물건번호: {}", cltrNo, e);
            throw e;
        }
    }
}
