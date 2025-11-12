package com.pgc.sideproj.dto.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponseDTO {

    private final int statusCode;

    private final String message;

    private final long timestamp;

    public static ErrorResponseDTO of(int statusCode, String message){
        return ErrorResponseDTO.builder()
                .statusCode(statusCode)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();

    }
}
