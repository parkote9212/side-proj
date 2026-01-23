package com.pgc.sideproj.exception.custom;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String fieldName;
    private final Object fieldValue;

    public UserNotFoundException(String fieldName, Object fieldValue) {
        super(String.format("사용자를 찾을 수 없습니다 (%s: %s)", fieldName, fieldValue));
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
