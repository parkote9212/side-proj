package com.pgc.sideproj.exception.custom;

import lombok.Getter;

@Getter
public class DuplicateSavedItemException extends RuntimeException {
    private final Long userId;
    private final String itemId;

    public DuplicateSavedItemException(Long userId, String itemId) {
        super(String.format("이미 찜 목록에 추가된 물건입니다. (userId: %d, itemId: %s)", userId, itemId));
        this.userId = userId;
        this.itemId = itemId;
    }
}
