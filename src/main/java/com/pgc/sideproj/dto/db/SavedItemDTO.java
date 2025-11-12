package com.pgc.sideproj.dto.db;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SavedItemDTO {

    private Long id;

    // DB 필드명: user_id
    private Long userId;

    // DB 필드명: item_id (cltr_no에서 변경)
    private String itemId;

    // DB 필드명: created_at
    private LocalDateTime createdAt;
}