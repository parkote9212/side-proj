-- ========================================
-- 추가 인덱스 생성 스크립트
-- 성능 최적화를 위한 인덱스 추가
-- ========================================

USE `onbid_db`;

-- 1. auction_master 테이블 인덱스
-- 지역별 검색 최적화
CREATE INDEX IF NOT EXISTS `idx_cln_ldnm_adrs_prefix` ON `auction_master` (`cln_ldnm_adrs`(50));

-- 좌표 기반 검색 최적화 (지도 범위 검색)
CREATE INDEX IF NOT EXISTS `idx_latitude_longitude` ON `auction_master` (`latitude`, `longitude`);

-- 카테고리별 검색 최적화
CREATE INDEX IF NOT EXISTS `idx_ctgr_full_nm` ON `auction_master` (`ctgr_full_nm`);

-- 2. auction_history 테이블 인덱스
-- 이미 idx_cltr_no와 idx_pbct_cls_dtm이 있지만, 복합 인덱스 추가
CREATE INDEX IF NOT EXISTS `idx_cltr_no_pbct_cls_dtm` ON `auction_history` (`cltr_no`, `pbct_cls_dtm` DESC);

-- 3. user 테이블 인덱스
-- 이미 idx_email이 있음 (UNIQUE 제약조건으로 자동 생성)

-- 4. saved_item 테이블 인덱스
-- 이미 idx_user_id, idx_item_id, uk_user_item이 있음

-- ========================================
-- 인덱스 확인
-- ========================================
-- SHOW INDEX FROM auction_master;
-- SHOW INDEX FROM auction_history;
-- SHOW INDEX FROM user;
-- SHOW INDEX FROM saved_item;
