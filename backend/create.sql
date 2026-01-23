-- ========================================
-- MariaDB Database Creation Script
-- Database: onbid_db
-- ========================================
-- 이 스크립트는 데이터베이스와 모든 테이블, 인덱스를 생성합니다.
-- 배포 전 초기 설정에 사용하세요.

-- ========================================
-- 1. 데이터베이스 생성
-- ========================================
CREATE DATABASE IF NOT EXISTS `onbid_db`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `onbid_db`;

-- ========================================
-- 2. 테이블 생성
-- ========================================

-- 2.1. 공매 물건 마스터 테이블 (불변 정보)
-- ========================================
CREATE TABLE IF NOT EXISTS `auction_master` (
    `cltr_no` VARCHAR(50) NOT NULL PRIMARY KEY COMMENT '물건번호',
    `cltr_nm` VARCHAR(1000) COMMENT '물건명',
    `ctgr_full_nm` VARCHAR(255) COMMENT '카테고리 전체명',
    `ldnm_adrs` VARCHAR(1000) COMMENT '원본 지번주소',
    `nmrd_adrs` VARCHAR(1000) COMMENT '원본 도로명주소',
    `cln_ldnm_adrs` VARCHAR(500) COMMENT '정제된 지번주소',
    `cln_nmrd_adrs` VARCHAR(500) COMMENT '정제된 도로명주소',

    -- 지도 좌표
    `latitude` DECIMAL(10, 8) COMMENT '위도 (Y)',
    `longitude` DECIMAL(11, 8) COMMENT '경도 (X)',

    -- 온비드 관련
    `plnm_no` VARCHAR(50) COMMENT '공고번호',
    `pbct_no` VARCHAR(50) COMMENT '공매번호',
    `onbid_detail_url` VARCHAR(500) COMMENT '온비드 상세 URL',

    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    -- Full-Text Search 인덱스
    FULLTEXT KEY `ft_idx_address_name` (`cln_ldnm_adrs`, `cln_nmrd_adrs`, `cltr_nm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='공매 물건 마스터';

-- 2.2. 공매 물건 이력 테이블 (가변 정보)
-- ========================================
CREATE TABLE IF NOT EXISTS `auction_history` (
    `cltr_hstr_no` VARCHAR(50) NOT NULL PRIMARY KEY COMMENT '물건이력번호',
    `cltr_no` VARCHAR(50) NOT NULL COMMENT '물건번호 (FK)',
    `min_bid_prc` BIGINT COMMENT '최저입찰가',
    `apsl_ases_avg_amt` BIGINT COMMENT '감정가',
    `pbct_begn_dtm` DATETIME COMMENT '입찰시작일시',
    `pbct_cls_dtm` DATETIME COMMENT '입찰마감일시',
    `pbct_cltr_stat_nm` VARCHAR(100) COMMENT '물건상태',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    KEY `idx_cltr_no` (`cltr_no`),
    KEY `idx_pbct_cls_dtm` (`pbct_cls_dtm`),
    CONSTRAINT `fk_auction_history_master`
        FOREIGN KEY (`cltr_no`) REFERENCES `auction_master`(`cltr_no`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='공매 물건 이력';

-- 2.3. 사용자 테이블
-- ========================================
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '이메일 (로그인 ID)',
    `password` VARCHAR(255) NOT NULL COMMENT '비밀번호 (BCrypt 해시)',
    `nickname` VARCHAR(100) COMMENT '닉네임',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '권한 (USER/ADMIN)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '가입일시',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='사용자';

-- 2.4. 찜 목록 테이블
-- ========================================
CREATE TABLE IF NOT EXISTS `saved_item` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '사용자 ID',
    `item_id` VARCHAR(50) NOT NULL COMMENT '물건번호 (auction_master.cltr_no)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '찜한 날짜',

    KEY `idx_user_id` (`user_id`),
    KEY `idx_item_id` (`item_id`),
    UNIQUE KEY `uk_user_item` (`user_id`, `item_id`),

    CONSTRAINT `fk_saved_item_user`
        FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_saved_item_auction`
        FOREIGN KEY (`item_id`) REFERENCES `auction_master`(`cltr_no`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='찜 목록';

-- 2.5. ShedLock 테이블 (배치 스케줄러용)
-- ========================================
CREATE TABLE IF NOT EXISTS `shedlock` (
    `name` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '락 이름',
    `lock_until` TIMESTAMP(3) NOT NULL COMMENT '락 만료 시각',
    `locked_at` TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '락 획득 시각',
    `locked_by` VARCHAR(255) NOT NULL COMMENT '락 획득 인스턴스'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='ShedLock 분산 락';

-- ========================================
-- 3. 추가 인덱스 생성 (성능 최적화)
-- ========================================

-- 3.1. auction_master 테이블 추가 인덱스
-- ========================================
-- 지역별 검색 최적화
CREATE INDEX IF NOT EXISTS `idx_cln_ldnm_adrs_prefix` ON `auction_master` (`cln_ldnm_adrs`(50));

-- 좌표 기반 검색 최적화 (지도 범위 검색)
CREATE INDEX IF NOT EXISTS `idx_latitude_longitude` ON `auction_master` (`latitude`, `longitude`);

-- 카테고리별 검색 최적화
CREATE INDEX IF NOT EXISTS `idx_ctgr_full_nm` ON `auction_master` (`ctgr_full_nm`);

-- 3.2. auction_history 테이블 추가 인덱스
-- ========================================
-- 복합 인덱스 (물건번호 + 마감일시)
CREATE INDEX IF NOT EXISTS `idx_cltr_no_pbct_cls_dtm` ON `auction_history` (`cltr_no`, `pbct_cls_dtm` DESC);

-- ========================================
-- 4. 초기 데이터 삽입
-- ========================================

-- 관리자 계정 생성
-- 비밀번호: admin123 (BCrypt 해시)
-- 실제 사용 시 애플리케이션에서 BCrypt로 해시된 비밀번호로 변경 필요
INSERT INTO `user` (`email`, `password`, `nickname`, `role`)
VALUES ('admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '관리자', 'ADMIN')
ON DUPLICATE KEY UPDATE `email`=`email`;

-- ========================================
-- 5. 인덱스 확인 (주석 처리)
-- ========================================
-- 다음 쿼리로 인덱스 확인 가능:
-- SHOW INDEX FROM auction_master;
-- SHOW INDEX FROM auction_history;
-- SHOW INDEX FROM user;
-- SHOW INDEX FROM saved_item;
-- SHOW INDEX FROM shedlock;

-- ========================================
-- 완료
-- ========================================
-- Database creation completed successfully
