CREATE DATABASE onbid_db;
-- 1. 공매 물건 마스터 (불변 정보)
CREATE TABLE auction_master (
    cltr_no VARCHAR(50) NOT NULL PRIMARY KEY,   -- 물건번호
    cltr_nm VARCHAR(1000),                      -- 물건명 (FTS 대상)
    ctgr_full_nm VARCHAR(255),                  -- 카테고리
    ldnm_adrs VARCHAR(1000),                    -- 원본 지번주소
    nmrd_adrs VARCHAR(1000),                    -- 원본 도로명주소
    cln_ldnm_adrs VARCHAR(500),                 -- 정제된 지번주소 (FTS 대상)
    cln_nmrd_adrs VARCHAR(500),                 -- 정제된 도로명주소 (FTS 대상)
    
    -- 지도 좌표
    latitude DECIMAL(10, 8),                    -- 위도 (Y)
    longitude DECIMAL(11, 8),                   -- 경도 (X)

    onbid_detail_url VARCHAR(500),              -- (생성 필요)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Full-Text Search 인덱스
    FULLTEXT KEY ft_idx_address_name (cln_ldnm_adrs, cln_nmrd_adrs, cltr_nm)
) ENGINE=InnoDB;

-- 2. 공매 물건 이력 (가변 정보)
CREATE TABLE auction_history (
    cltr_hstr_no VARCHAR(50) NOT NULL PRIMARY KEY, -- 물건이력번호
    cltr_no VARCHAR(50) NOT NULL,               -- 물건번호 (FK)
    min_bid_prc BIGINT,                         -- 최저입찰가
    apsl_ases_avg_amt BIGINT,                   -- 감정가
    pbct_begn_dtm DATETIME,                     -- 입찰시작일시
    pbct_cls_dtm DATETIME,                      -- 입찰마감일시
    pbct_cltr_stat_nm VARCHAR(100),             -- 물건상태
    FOREIGN KEY (cltr_no) REFERENCES auction_master(cltr_no)
) ENGINE=InnoDB;

-- 3. 사용자
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,        -- 이메일 (로그인 ID)
    password VARCHAR(255) NOT NULL,            -- (BCrypt로 해시됨)
    nickname VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 4. 찜 목록
CREATE TABLE saved_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_id VARCHAR(50) NOT NULL,  -- <-- [수정] BIGINT -> VARCHAR(50)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (item_id) REFERENCES auction_master(cltr_no),
    UNIQUE KEY uk_user_item (user_id, item_id)
);